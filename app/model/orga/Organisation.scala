// The MIT License (MIT)
//
// Copyright (c) 2013 Igor Laborie
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of
// this software and associated documentation files (the "Software"), to deal in
// the Software without restriction, including without limitation the rights to
// use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
// the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
// IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
// CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package model.orga

import play.api.cache.Cache
import play.api.Play.current
import model.event.Event
import util.Location


sealed trait PlayerContainer {
  def players: Seq[Player]

  def isMember(participant: Participant): Boolean = participant match {
    case p: Player => players.contains(p)
    case Pair(p1, p2) => players.contains(p1) && players.contains(p2)
    case TeamPair(p1, p2) => players.contains(p1) && players.contains(p2)
    case _ => false
  }
}

/**
 * Ligue
 * @param name name
 * @param shortName shortName
 * @param comites comites
 * @param coupe coupe
 * @param master master
 * @param info information
 */
case class Ligue(name: String,
                 shortName: String,
                 comites: Seq[Comite],
                 opens: Seq[OpenLigue],
                 coupe: CoupeLigue,
                 master: MasterLigue,
                 masterTeam: MasterLigueTeam,
                 coupeTeam: Option[CoupeLigueTeam] = None,
                 nationalTournaments: Seq[NationalTournament] = Nil,
                 info: Option[Info] = None) extends PlayerContainer {


  lazy val fullName = s"[$shortName] $name"

  val comiteRankings = {
    val dateRanking = master.date.plusDays(-1)
    ComiteRank(dateRanking)
  }

  override def toString = fullName

  def findTournamentByShortName(sname: String): Option[LigueTournament] = tournaments.find(_.shortName == sname)

  def findComiteByShortName(sname: String): Option[Comite] = comites.find(_.shortName == sname)

  def findTeamByShortName(sname: String): Option[Team] = teams.find(_.shortName == sname)

  lazy val clubs = {
    for {
      comite <- comites
      club <- comite.clubs
    } yield club
  }

  lazy val allTeams = {
    for {
      club <- clubs
      team <- club.teams
    } yield team
  }

  lazy val teams = allTeams.filter(!_.omit)

  lazy val players = {
    for {
      team <- teams
      player <- team.players
    } yield player
  }

  lazy val tournaments: List[LigueTournament] = {
    val clTeam = coupeTeam.toList
    val list = (coupe :: master :: masterTeam :: comiteRankings :: opens.toList) ::: clTeam ::: nationalTournaments.toList
    list.sortBy(_.date)
  }

  lazy val events = for {
    tournament <- this.tournaments
    if tournament.isEvent
  } yield Event(this, tournament)
}

object Ligue {
  lazy val nlPlayers: Seq[NotLicensedPlayer] = Data.readNotLicensedPlayers(Season.currentSeason)

  lazy val ligues: Seq[Ligue] = Data.readLigues(Season.currentSeason)

  def findPlayerByName(name: String): Option[Player] = (players ++ nlPlayers).find(_.name == name)

  lazy val comites = {
    for {
      ligue <- ligues
      comite <- ligue.comites
    } yield comite
  }

  lazy val clubs = {
    for {
      ligue <- ligues
      club <- ligue.clubs
    } yield club
  }

  lazy val allTeams = {
    for {
      ligue <- ligues
      team <- ligue.teams
    } yield team
  }

  lazy val teams = allTeams.filter(!_.omit)

  lazy val players = {
    for {
      ligue <- ligues
      player <- ligue.players
    } yield player
  }

  def findByShortName(sname: String): Option[Ligue] = ligues.find(_.shortName == sname)
}

/**
 * Comite
 * @param name name
 * @param shortName short name
 * @param clubs clubs
 * @param coupe coupe
 * @param info information
 */
case class Comite(name: String,
                  shortName: String,
                  clubs: Seq[Club],
                  coupe: CoupeComite,
                  info: Option[Info]) extends PlayerContainer{
  lazy val fullName = s"[$shortName] $name"

  override def toString = fullName

  def findClubByShortName(sname: String): Option[Club] = clubs.find(_.shortName == sname)

  def findTournamentByShortName(sname: String): Option[ComiteTournament] = tournaments.find(_.shortName == sname)

  lazy val allTeams = {
    for {
      club <- clubs
      team <- club.teams
    } yield team
  }
  lazy val teams = allTeams.filter(!_.omit)

  lazy val players = {
    for {
      team <- teams
      player <- team.players
    } yield player
  }

  lazy val tournaments: List[ComiteTournament] = {
    val seq = for {
      club <- clubs
      open <- club.opens
    } yield open

    (coupe :: seq.toList).sortBy(_.date)
  }

  def ligue: Ligue = Cache.getOrElse[Ligue](s"comite.$shortName.ligue") {
    Ligue.ligues.find(_.comites.contains(this)).get
  }

  lazy val events = for {
    tournament <- this.tournaments
  } yield Event(this, tournament)
}


/**
 * Club
 * @param name name
 * @param shortName short name
 * @param opens opens
 * @param teams teams
 * @param info information
 */
case class Club(name: String,
                shortName: String,
                location: Location,
                opens: Seq[OpenClub],
                teams: Seq[Team],
                info: Option[Info]) extends PlayerContainer{
  lazy val fullName = s"[$shortName] $name"

  override def toString = fullName

  def findTeamByShortName(teamShortName: String): Option[Team] = teams.find(_.shortName == teamShortName)

  lazy val players = {
    for {
      team <- teams
      player <- team.players
    } yield player
  }

  def ligue: Ligue = Cache.getOrElse[Ligue](s"club.$shortName.ligue") {
    Ligue.ligues.find(_.clubs.contains(this)).get
  }

  def comite: Comite = Cache.getOrElse[Comite](s"club.$shortName.comite") {
    ligue.comites.find(_.clubs.contains(this)).get
  }

  lazy val events = for (open <- opens) yield Event(comite, open)

}


/**
 * Team
 * @param name name
 * @param shortName short name
 * @param players team players
 * @param omit if not playing the team championship
 */
case class Team(name: String, shortName: String, capitainName: String, players: Seq[LicensedPlayer], omit: Boolean = false) {
  override val toString = name

  lazy val capitain: LicensedPlayer = club.players.find(_.name == capitainName).get

  lazy val ligue: Ligue = Ligue.ligues.find(_.allTeams.contains(this)).get

  lazy val comite: Comite = ligue.comites.find(_.allTeams.contains(this)).get

  lazy val club: Club = comite.clubs.find(_.teams.contains(this)).get

}

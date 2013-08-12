package model.orga

import play.api.cache.Cache
import play.api.Play.current
import model.event.Event
import util.Location

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
                 info: Option[Info]) {

  lazy val fullName = s"[$shortName] $name"

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

  lazy val teams = {
    for {
      club <- clubs
      team <- club.teams
      if !team.omit
    } yield team
  }

  lazy val players = {
    for {
      team <- teams
      player <- team.players
    } yield player
  }

  lazy val tournaments: List[LigueTournament] = {
    val dateRanking = master.date.plusDays(-1)
    val comiteRankings = for (comite <- comites) yield ComiteRank(comite, dateRanking)

    val list = (coupe :: master :: masterTeam :: opens.toList) ::: comiteRankings.toList
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

  lazy val teams = {
    for {
      ligue <- ligues
      team <- ligue.teams
      if !team.omit
    } yield team
  }

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
                  info: Option[Info]) {
  lazy val fullName = s"[$shortName] $name"

  override def toString = fullName

  def findClubByShortName(sname: String): Option[Club] = clubs.find(_.shortName == sname)

  def findTournamentByShortName(sname: String): Option[ComiteTournament] = tournaments.find(_.shortName == sname)

  lazy val teams = {
    for {
      club <- clubs
      team <- club.teams
      if !team.omit
    } yield team
  }

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
case class Club(name: String, shortName: String, location: Location, opens: Seq[OpenClub], teams: Seq[Team], info: Option[Info]) {
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
case class Team(name: String, shortName: String, players: Seq[LicensedPlayer], omit: Boolean = false) {
  override val toString = name

  def ligue: Ligue = Cache.getOrElse[Ligue](s"team.$name.ligue") {
    Ligue.ligues.find(_.teams.contains(this)).get
  }

  def comite: Comite = Cache.getOrElse[Comite](s"team.$name.comite") {
    ligue.comites.find(_.teams.contains(this)).get
  }

  def club: Club = Cache.getOrElse[Club](s"team.$name.club") {
    comite.clubs.find(_.teams.contains(this)).get
  }

}

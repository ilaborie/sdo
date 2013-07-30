package model.orga

import java.util.Calendar
import play.api.cache.Cache
import play.api.Play.current
import play.api.Logger

/**
 * Ligue
 * @param name name
 * @param shortName shortname
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
                 info: Option[Info]) {

  lazy val fullName = s"[$shortName] $name"

  override def toString = fullName

  def findComiteByShortName(shortName: String): Option[Comite] = comites.find(_.shortName == shortName)

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

  def findTeamByName(name: String): Option[Team] = Cache.getOrElse[Option[Team]](s"Ligue.team.$name") {
    teams.find(_.name == name)
  }

  lazy val players = {
    for {
      team <- teams
      player <- team.players
    } yield player
  }

  lazy val tournaments: List[LigueTournament] = {
    val comiteCoupes = for {
      comite <- comites
    } yield ComiteCoupeLigue(comite)

    val dateRanking = Calendar.getInstance()
    dateRanking.setTimeInMillis(master.date.getTimeInMillis)
    dateRanking.add(Calendar.DAY_OF_MONTH, -1)

    val comiteRankings = for {
      comite <- comites
    } yield ComiteRank(comite, dateRanking)

    val list = (coupe :: master :: opens.toList) ::: comiteCoupes.toList ::: comiteRankings.toList
    list.sortBy(_.date.getTimeInMillis)
  }
}

object Ligue {
  val ligues: Seq[Ligue] = Data.readLigues()

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

  def findByShortName(shortName: String): Option[Ligue] = Cache.getOrElse[Option[Ligue]](s"ligue.$shortName") {
    ligues.find(_.shortName == shortName)
  }
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

    (coupe :: seq.toList).sortBy(_.date.getTimeInMillis)
  }

  def ligue: Ligue = Cache.getOrElse[Ligue](s"comite.$shortName.ligue") {
    Ligue.ligues.find(_.comites.contains(this)).get
  }
}

/**
 * Club
 * @param name name
 * @param shortName short name
 * @param opens opens
 * @param teams teams
 * @param info information
 */
case class Club(name: String, shortName: String, opens: Seq[OpenClub], teams: Seq[Team], info: Option[Info]) {
  lazy val fullName = s"[$shortName] $name"

  override def toString = fullName

  def findTeamByName(name: String): Option[Team] = Cache.getOrElse[Option[Team]](s"club.$shortName.team.$name") {
    teams.find(_.name == name)
  }

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
}

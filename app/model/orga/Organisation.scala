package model.orga

import model.tournament._
import java.util.Calendar

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

  lazy val players = {
    for {
      comite <- comites
      club <- comite.clubs
      team <- club.teams
      player <- team.players
    } yield player
  }

  lazy val tournaments: List[LigueTournament] = {
    val comiteCoupes = for {
      comite <- comites
    } yield ComiteCoupeLigue(comite.coupe.date)

    val dateRanking = Calendar.getInstance()
    dateRanking.setTimeInMillis(master.date.getTimeInMillis)
    dateRanking.add(Calendar.DAY_OF_MONTH, -1)

    val comiteRankings = for {
      comite <- comites
    } yield ComiteRanking(dateRanking)

    val list = (coupe :: master :: opens.toList) ::: comiteCoupes.toList ::: comiteRankings.toList
    list.sortBy(_.date.getTimeInMillis)
  }
}

object Ligue {
  val all: Seq[Ligue] = Data.readLigues()

  def findByShortName(shortName: String): Option[Ligue] = all.find(_.shortName == shortName)
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

  def findClubByShortName(shortName: String): Option[Club] = clubs.find(_.shortName == shortName)

  lazy val players = {
    for {
      club <- clubs
      team <- club.teams
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

  def findTeamByName(name: String): Option[Team] = teams.find(_.name == name)

  lazy val players = {
    for {
      team <- teams
      player <- team.players
    } yield player
  }
}

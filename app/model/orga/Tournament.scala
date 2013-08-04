package model.orga

import java.util.Calendar
import play.api.cache.Cache
import play.api.Play.current
import play.api.i18n.Messages
import org.apache.commons.lang3.time.FastDateFormat

/**
 * Tournament
 */
sealed abstract class Tournament {
  def date: Calendar

  def getPoint(position: TournamentResult): Int

}

object Tournament {
  val orderByDate: Ordering[Tournament] = Ordering.by[Tournament, Long](_.date.getTimeInMillis)
}

/**
 * Ligue Tournament
 */
sealed abstract class LigueTournament extends Tournament {
  val isEvent: Boolean = true
  val isTeam: Boolean = false

  def shortName: String
}

/**
 * Open Ligue
 */
case class OpenLigue(date: Calendar, location: String) extends LigueTournament {

  override def toString = Messages("rank.ligue.open.title")


  val shortName = s"OL-${FastDateFormat.getInstance("yyyyMMdd").format(date)}"

  def getPoint(position: TournamentResult): Int = position match {
    case Winner => 16
    case RunnerUp => 11
    case SemiFinal => 7
    case QuarterFinal => 4
    case EighthFinal => 2
    case _ => 1
  }
}

/**
 * Coupe Ligue
 */
case class CoupeLigue(date: Calendar, location: String) extends LigueTournament {

  override def toString = Messages("rank.ligue.coupe.title")

  val shortName = "CL"

  def getPoint(position: TournamentResult): Int = position match {
    case Winner => 22
    case RunnerUp => 16
    case SemiFinal => 11
    case QuarterFinal => 7
    case EighthFinal => 4
    case SixteenthFinal => 2
    case ThirtySecondFinal => 1
    case _ => 0
  }
}

/**
 * Master Ligue
 */
case class MasterLigue(date: Calendar, location: String) extends LigueTournament {

  override def toString = Messages("rank.ligue.master.title")

  val shortName = "Ma"

  def getPoint(position: TournamentResult): Int = position match {
    case Winner => 29
    case RunnerUp => 22
    case SemiFinal => 16
    case QuarterFinal => 11
    case RoundRobin(pos) => if (pos == 3) 4 else 2
    case _ => 0
  }
}

/**
 * Master Ligue Team
 */
case class MasterLigueTeam(date: Calendar, location: String) extends LigueTournament {
  override val isTeam: Boolean = true
  val shortName = "MaT"

  override def toString = Messages("rank.ligue.master.team.title")

  def getPoint(position: TournamentResult): Int = ???
}

/**
 * Comite Ranking
 */
case class ComiteRank(comite: Comite, date: Calendar) extends LigueTournament {

  override def toString = Messages("rank.ligue.comite.rank.title", comite)
  val shortName = "LCR"

  override val isEvent: Boolean = false

  def getPoint(position: TournamentResult): Int = position match {
    case RoundRobin(pos) => pos match {
      case 1 => 8
      case 2 => 7
      case 3 => 6
      case 4 => 5
      case 5 => 4
      case 6 => 3
      case 7 => 2
      case 8 => 1
      case _ => 0
    }
    case _ => 0
  }
}

/**
 * Coupe Comite
 */
case class ComiteCoupeLigue(comite: Comite) extends LigueTournament {
  override def toString = Messages("rank.ligue.comite.coupe.title", comite)

  val shortName = "LCC"

  override val isEvent: Boolean = false

  val date: Calendar = comite.coupe.date

  def getPoint(position: TournamentResult): Int = position match {
    case Winner => 29
    case RunnerUp => 22
    case SemiFinal => 16
    case QuarterFinal => 11
    case RoundRobin(pos) => if (pos == 3) 4 else 2
    case _ => 0
  }

}


/**
 * Comite Tournament
 */
sealed abstract class ComiteTournament extends Tournament {
  def shortName: String
}

/**
 * Coupe Comite
 */
case class CoupeComite(date: Calendar, location: String) extends ComiteTournament {
  override def toString = Messages("rank.comite.coupe.title")
  val shortName = "CC"

  def getPoint(position: TournamentResult): Int = position match {
    case Winner => 29
    case RunnerUp => 22
    case SemiFinal => 16
    case QuarterFinal => 11
    case RoundRobin(pos) => if (pos == 3) 4 else 2
    case _ => 0
  }
}

/**
 * Open Club
 */
case class OpenClub(date: Calendar) extends ComiteTournament {
  override def toString = Messages("rank.comite.open.title", club.name)

  val shortName = s"OC-${FastDateFormat.getInstance("yyyyMMdd").format(date)}"

  def club: Club = Cache.getOrElse[Club](s"openClub.$date.club") {
    Ligue.clubs.find(_.opens.contains(this)).get
  }

  def getPoint(position: TournamentResult): Int = position match {
    case Winner => 16
    case RunnerUp => 11
    case SemiFinal => 7
    case QuarterFinal => 4
    case EighthFinal => 2
    case _ => 1
  }
}





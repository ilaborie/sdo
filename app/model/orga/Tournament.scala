package model.orga

import play.api.i18n.Messages
import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import util.Location

/**
 * Tournament
 */
sealed abstract class Tournament {
  def date: LocalDate

  def getPoint(position: TournamentResult): Int

  def shortName: String

  def place: Option[Location]

  def ligue: Ligue
  def maybyComite: Option[Comite]
}

object Tournament {

  val orderByDate: Ordering[Tournament] = Ordering.by[Tournament, LocalDate](_.date)
}

/**
 * Ligue Tournament
 */
sealed abstract class LigueTournament extends Tournament {
  val isEvent: Boolean = true
  val isTeam: Boolean = false
  val maybyComite = None
}

/**
 * Open Ligue
 */
case class OpenLigue(date: LocalDate, location: Location) extends LigueTournament {

  override def toString = Messages("rank.ligue.open.title", ligue.name)

  val place= Some(location)

  lazy val ligue = Ligue.ligues.find(_.opens.contains(this)).get

  val shortName = s"OL-${DateTimeFormat.forPattern("yyyyMMdd").print(date)}"

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
case class CoupeLigue(date: LocalDate, location: Location) extends LigueTournament {

  override def toString = Messages("rank.ligue.coupe.title", ligue.name)

  val place = Some(location)

  val shortName = "CL"

  lazy val ligue = Ligue.ligues.find(_.coupe == this).get

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
case class MasterLigue(date: LocalDate, location: Location) extends LigueTournament {

  override def toString = Messages("rank.ligue.master.title")

  val place = Some(location)

  val shortName = "Mast"

  lazy val ligue = Ligue.ligues.find(_.master == this).get

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
case class MasterLigueTeam(date: LocalDate, location: Location) extends LigueTournament {
  override val isTeam: Boolean = true
  val shortName = "MastTeam"
  lazy val ligue = Ligue.ligues.find(_.masterTeam == this).get
  val place = Some(location)

  override def toString = Messages("rank.ligue.master.team.title")

  def getPoint(position: TournamentResult): Int = 0
}
/**
 * Coupe Ligue Team
 */
case class CoupeLigueTeam(date: LocalDate, location: Location) extends LigueTournament {
  override val isTeam: Boolean = true
  val shortName = "CLTeam"
  lazy val ligue = Ligue.ligues.find(_.coupeTeam == Some(this)).get
  val place = Some(location)

  override def toString = Messages("rank.ligue.coupe.team.title")

  def getPoint(position: TournamentResult): Int = 0
}

/**
 * Comite Ranking
 */
case class ComiteRank(comite: Comite, date: LocalDate) extends LigueTournament {

  override def toString = Messages("rank.ligue.comite.rank.title", comite.name)

  val place = None

  lazy val ligue = comite.ligue

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
 * National Tournament
 * @param shortName shortName
 * @param date date
 */
case class NationalTournament(shortName:String,date: LocalDate) extends LigueTournament {
  override def toString = Messages(s"rank.ligue.national.${shortName.toLowerCase}.title")

  val place = None

  lazy val ligue: Ligue = Ligue.ligues.find(_.nationalTournaments.contains(this)).get

  override val isEvent: Boolean = false

  def getPoint(position: TournamentResult): Int = position match {
    case WinningMatch(win) => 1 + win
    case _ => 0
  }
}

/**
 * Comite Tournament
 */
sealed abstract class ComiteTournament extends Tournament {
  def shortName: String

  def comite: Comite
  lazy val ligue = comite.ligue
  lazy val maybyComite = Some(comite)
}

/**
 * Coupe Comite
 */
case class CoupeComite(date: LocalDate, location: Location) extends ComiteTournament {
  override def toString = Messages("rank.comite.coupe.title", comite.name)

  val shortName = "CC"
  val place = Some(location)

  lazy val comite: Comite = Ligue.comites.find(_.coupe == this).get

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
case class OpenClub(date: LocalDate, location: Location) extends ComiteTournament {
  override def toString = Messages("rank.comite.open.title", club.name)

  val place = Some(location)
  val shortName = s"OC-${DateTimeFormat.forPattern("yyyyMMdd").print(date)}"

  lazy val club: Club = Ligue.clubs.find(_.opens.contains(this)).get
  lazy val comite = club.comite

  def getPoint(position: TournamentResult): Int = position match {
    case Winner => 16
    case RunnerUp => 11
    case SemiFinal => 7
    case QuarterFinal => 4
    case EighthFinal => 2
    case _ => 1
  }
}





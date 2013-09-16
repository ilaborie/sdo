package model.event


import model.orga._
import model.team._
import play.api.i18n.Messages
import org.joda.time.{YearMonth, LocalDate}

import util._

/**
 * Event
 * @param from start date
 * @param eventType type
 * @param to end date
 * @param location the location
 * @param name a name
 * @param email maybe an email address
 * @param url maybe an URL
 * @param info maybe some ligue
 */
case class Event(name: String,
                 eventType: EventType,
                 from: LocalDate,
                 to: LocalDate,
                 location: Option[Location] = None,
                 email: Option[EMail] = None,
                 url: Option[String] = None,
                 info: Option[Info] = None) {

  override val toString = name

  def applyTo(yearMonth: YearMonth): Boolean = {
    val d0 = yearMonth.toInterval.getStart.toLocalDate
    val d1 = yearMonth.toInterval.getEnd.toLocalDate

    def isInclude(date: LocalDate) = {
      (date.isEqual(d0) || date.isAfter(d0)) && (date.isEqual(d1) || date.isBefore(d1))
    }

    isInclude(from) || isInclude(to) || (from.isBefore(d0) && to.isAfter(d1))
  }

  def applyTo(date: LocalDate): Boolean =
    (from.isEqual(date) || to.isEqual(date)) || (from.isBefore(date) && to.isAfter(date))
}

object Event {

  implicit def dateTimeOrdering: Ordering[LocalDate] = Ordering.fromLessThan(_ isBefore _)

  val orderByStartDate: Ordering[Event] = Ordering.by[Event, LocalDate](_.from)

  /** All Events */
  lazy val events: Seq[Event] = {
    val season = Season.currentSeason
    // ligues
    val ligueEvents: Seq[Event] = for {
      ligue <- Ligue.ligues
      event <- ligue.events
    } yield event

    // comites
    val comiteEvents: Seq[Event] = for {
      ligue <- Ligue.ligues
      comite <- ligue.comites
      event <- comite.events
    } yield event

    // Team
    val teamEvents: Seq[Event] = for {
      ligue <- Ligue.ligues
      event <- DataChampionship.readChampionship(season, ligue).events
    } yield event

    ligueEvents ++ comiteEvents ++ teamEvents ++ DataEvent.readEvents(season)
  }.sorted(orderByStartDate)

  /**
   * Team event
   * @param ligue ligue
   * @param day day
   * @return Event
   */
  def apply(ligue: Ligue, day: TeamChampionshipDay): Event = {
    val name = Messages("team.championship.day", day.day)
    val url = s"/sdo/ligues/${ligue.shortName}#team"
    val matches = {
      for (ma <- day.matches)
      yield s"<tr><td>${ma.team1.name}</td><td>-</td><td>${ma.team2.name}</td</tr>"
    }.mkString("<tbody>", "", "</tbody>")

    val exempted = day.teamExempted
    val foot =
      if (exempted.isEmpty) ""
      else s"""<tfoot><tr><td colspan="2">${Messages("team.exempted", exempted.map(_.name).mkString(", "))}</td></tr></tfoot>"""

    val info = s"""
<table class="table table-striped table-condensed team-event">
$matches
$foot
</table>
"""
    Event(name, TeamEvent, day.from, day.to, url = Some(url), info = Some(info))
  }

  /**
   * Ligue Event
   * @param ligue ligue
   * @param tournament tournament
   * @return Event
   */
  def apply(ligue: Ligue, tournament: LigueTournament): Event = {
    val name = tournament.toString
    val url = s"/sdo/ligues/${ligue.shortName}#tour/${tournament.shortName}"
    Event(name, LigueEvent, tournament.date, tournament.date, url = Some(url), location = tournament.place)
  }

  /**
   * Comite Event
   * @param comite comite
   * @param tournament tournament
   * @return event
   */
  def apply(comite: Comite, tournament: ComiteTournament): Event = {
    val name = tournament.toString
    val url = s"/sdo/ligues/${comite.ligue.shortName}/comites/${comite.shortName}#tour/${tournament.shortName}"

    Event(name, ComiteEvent, tournament.date, tournament.date, url = Some(url), location = tournament.place, info = tournament.info)
  }
}

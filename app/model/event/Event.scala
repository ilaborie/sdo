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

  def apply(ligue: Ligue, day: TeamChampionshipDay): Event = {
    val name = Messages("team.championship.day", day.day)
    val url = controllers.routes.Orga.ligue(ligue.shortName).url + "#team"
    val matches = {
      for (ma <- day.matches)
      yield s"""
<div class="row-fluid">
  <div class="span5">${ma.team1.shortName}</div>
  <div class="span2">-</div>
  <div class="span5">${ma.team2.shortName}</div>
</div>"""
    }.mkString("\n")

    val exempted = day.teamExempted
    val info =
      if (exempted.isEmpty) matches
      else matches + Messages("team.exempted", exempted.map(_.shortName).mkString(", "))

    Event(name, TeamEvent, day.from, day.to, url = Some(url), info = Some(info))
  }


  def apply(ligue: Ligue, tournament: LigueTournament): Event = {
    val name = tournament.toString
    val url = controllers.routes.Orga.ligue(ligue.shortName).url + "#" + tournament.shortName

    Event(name, LigueEvent, tournament.date, tournament.date, url = Some(url), location = tournament.place)
  }

  def apply(comite: Comite, tournament: ComiteTournament): Event = {
    val name = tournament.toString
    val url = controllers.routes.Orga.comite(comite.ligue.shortName, comite.shortName).url + "#" + tournament.shortName

    Event(name, ComiteEvent, tournament.date, tournament.date, url = Some(url), location = tournament.place)
  }
}

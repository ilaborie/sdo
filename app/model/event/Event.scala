package model.event

import model.contact._
import model.orga._
import model.team._
import play.api.i18n.Messages
import org.joda.time.{Interval, LocalDate}


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

  private val interval: Interval = new Interval(from.toDate.getTime, to.toDate.getTime)

  def applyTo(anotherInterval: Interval): Boolean = interval overlaps anotherInterval

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
    // FIXME reverse routing, ligue, team
    val url = controllers.routes.Application.ligue(ligue.shortName).url
    val info = {
      for (ma <- day.matches)
      yield s"""<div>${ma.team1.name} - ${ma.team2.name}</div>"""
    }.mkString("")
    println(info)
    Event(name, TeamEvent, day.from, day.to, info = Some(info))
  }


  def apply(ligue: Ligue, tournament: LigueTournament): Event = {
    val name = tournament.toString
    // FIXME reverse routing, ligue, tournament
    val url = controllers.routes.Application.ligue(ligue.shortName).url

    Event(name, LigueEvent, tournament.date, tournament.date)
  }

  def apply(comite: Comite, tournament: ComiteTournament): Event = {
    val name = tournament.toString
    // FIXME reverse routing, comite, tournament
    val url = controllers.routes.Application.comite(comite.ligue.shortName, comite.shortName).url

    Event(name, ComiteEvent, tournament.date, tournament.date)
  }
}

/**
 * Location
 * @param name name
 * @param venue venue
 */
case class Location(name: String, venue: Option[String])

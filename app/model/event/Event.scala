package model.event

import java.util.Calendar

import model.contact._
import model.orga._
import model.team._
import play.api.i18n.Messages


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
                 from: Calendar,
                 to: Calendar,
                 location: Option[Location] = None,
                 email: Option[EMail] = None,
                 url: Option[String] = None,
                 info: Option[Info] = None) {

}

object Event {
  val orderByStartDate: Ordering[Event] = Ordering.by[Event, Long](_.from.getTimeInMillis)

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
    
    Event(name, TeamEvent, day.from, day.to)
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

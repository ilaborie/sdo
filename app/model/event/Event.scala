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
 * @param info maybe some info
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
      tournament <- ligue.tournaments
      if tournament.isEvent
    } yield Event(ligue, tournament)

    // comites
    val comiteEvents: Seq[Event] = for {
      ligue <- Ligue.ligues
      comite <- ligue.comites
      tournament <- comite.tournaments
    } yield Event(comite, tournament)

    // Team
    val teamEvents: Seq[Event] = for {
      ligue <- Ligue.ligues
      day <- DataChampionship.readChampionship(season, ligue).days
    } yield Event(ligue, day)

    ligueEvents ++ comiteEvents ++ teamEvents ++ DataEvent.readEvents(season)
  }.sorted(orderByStartDate)

  def apply(ligue: Ligue, day: TeamChampionshipDay): Event = {
    val name = Messages("team.championship.day", day.day)
    // FIXME reverse routing, ligue, team
    val url = controllers.routes.Application.ligue(ligue.shortName).url

    Event(name, TeamEvent, day.from, day.to, url = Some(url))
  }

  def apply(ligue: Ligue, tournament: LigueTournament): Event = {
    val name = tournament.toString
    // FIXME reverse routing, ligue, tournament
    val url = controllers.routes.Application.ligue(ligue.shortName).url

    Event(name, LigueEvent, tournament.date, tournament.date, url = Some(url))
  }

  def apply(comite: Comite, tournament: ComiteTournament): Event = {
    val name = tournament.toString
    // FIXME reverse routing, comite, tournament
    val url = controllers.routes.Application.comite(comite.ligue.shortName, comite.shortName).url

    Event(name, ComiteEvent, tournament.date, tournament.date, url = Some(url))
  }
}

/**
 * Location
 * @param name name
 * @param venue venue
 */
case class Location(name: String, venue: Option[String])

package model.event

import model.contact._
import model.contact.EMail
import java.util.Calendar
import model.orga.Season


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
case class Event(name: String, eventType: String, from: Calendar, to: Calendar, location: Option[Location], email: Option[EMail], url: Option[String], info: Option[Info]) {

}

object Event {
  val events: Seq[Event] = DataEvent.readEvents(Season.currentSeason)
}

case class Location(name: String, venue: Option[String])

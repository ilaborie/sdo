package controllers

import play.api.mvc._
import model.event.EventYear
import org.joda.time.LocalDate

/**
 * Mains pages
 */
object Event extends Controller {

  /**
   * Events
   * @return events page
   */
  def event() = Action {
    Ok(views.html.event.events())
  }

  /**
   * Events List
   * @return list page
   */
  def eventsList() = Action {
    Ok(views.html.event.list(model.event.Event.events))
  }


  /**
   * Events List
   * @return list page
   */
  def icalendar() = Action {
    val today = LocalDate.now
    val events = model.event.Event.events.filter(_.to.isAfter(today))
    Ok(views.html.event.icalendar(events)).withHeaders("Content-Type" -> "text/calendar; charset=UTF-8")
  }


  /**
   * Events calendar
   * @return calendar page
   */
  def eventsCalendar() = Action {
    val years = EventYear.years(model.event.Event.events)
    Ok(views.html.event.calendar(years))
  }

}

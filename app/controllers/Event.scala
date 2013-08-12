package controllers

import play.api.mvc._
import model.event.EventYear

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
   * Events calendar
   * @return calendar page
   */
  def eventsCalendar() = Action {
    val years = EventYear.years(model.event.Event.events)
    Ok(views.html.event.calendar(years))
  }

}

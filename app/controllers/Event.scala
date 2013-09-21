// The MIT License (MIT)
//
// Copyright (c) 2013 Igor Laborie
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of
// this software and associated documentation files (the "Software"), to deal in
// the Software without restriction, including without limitation the rights to
// use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
// the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
// IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
// CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

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

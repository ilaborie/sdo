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

package model.event

import org.joda.time.{LocalDate, YearMonth, DateTimeConstants}
import org.joda.time.format.DateTimeFormat

/**
 * Year Events
 * @param year year
 * @param months months
 */
case class EventYear(year: Int, months: List[EventMonth]) {
  override val toString = year.toString
}

object EventYear {

  def years(events: Seq[Event]): List[EventYear] = {
    // Starting/End Date
    val startDate = events.map(_.from).min
    val endDate = events.map(_.to).max

    // From min date to max date
    val years = for (year <- startDate.getYear to endDate.getYear) yield {

      val startMonth = if (startDate.getYear == year) startDate.getMonthOfYear else DateTimeConstants.JANUARY
      val endMonth = if (endDate.getYear == year) endDate.getMonthOfYear else DateTimeConstants.DECEMBER

      val months = for (month <- startMonth to endMonth) yield {
        val ym = new YearMonth(year, month)

        val monthEvents: Seq[Event] = events.filter(_.applyTo(ym))
        EventMonth(ym, monthEvents)
      }
      EventYear(year, months.toList)
    }
    years.toList
  }
}

/**
 * Month Event
 */
case class EventMonth(yearMonth: YearMonth, weeks: List[EventWeek]) {
  override val toString = DateTimeFormat.forPattern("MMMM").print(yearMonth).capitalize
}

object EventMonth {
  def apply(yearMonth: YearMonth, events: Seq[Event]): EventMonth = {
    val interval = yearMonth.toInterval
    val monthStart = interval.getStart.toLocalDate
    val monthEnd = interval.getEnd.toLocalDate.minusDays(1)

    // From create Days event
    val days = for (d <- monthStart.getDayOfMonth to monthEnd.getDayOfMonth) yield {
      val date = yearMonth.toLocalDate(d)
      val dayEvents = events.filter(_.applyTo(date))
      MonthDay(date, dayEvents)
    }

    val weeks: List[EventWeek] = {
      //  group by week
      val weeksDays: List[(Int, Seq[MonthDay])] = days.groupBy(_.date.getWeekOfWeekyear)
        .toList
        .sorted(Ordering.by((x: (Int, Seq[MonthDay])) => x._2.head.date))
      // build week
      for ((w, md) <- weeksDays) yield {
        EventWeek(yearMonth, w, md)
      }
    }
    EventMonth(yearMonth, weeks)
  }
}

/**
 * Week Event
 * @param days days
 */
case class EventWeek(days: List[EventDay])

object EventWeek {
  private def padLeft(list: List[EventDay]): List[EventDay] = {
    if (list.size < 7) padLeft(Nope :: list)
    else list
  }

  private def padRight(list: List[EventDay]): List[EventDay] = {
    if (list.size < 7) padRight(list :+ Nope)
    else list
  }

  def apply(yearMonth: YearMonth, week: Int, events: Seq[MonthDay]): EventWeek = {
    val lst = events.sorted(Ordering.by((md: MonthDay) => md.date))
    if (week == yearMonth.toInterval.getStart.getWeekOfWeekyear) EventWeek(padLeft(lst.toList))
    else EventWeek(padRight(lst.toList))
  }
}

/**
 * Day Event
 */
abstract sealed class EventDay {
  def holiday: Boolean
}

case object Nope extends EventDay {
  val holiday = false
}

abstract class MonthDay extends EventDay {
  def date: LocalDate

  def holiday: Boolean = {
    (date.getDayOfMonth == 1 && date.getMonthOfYear == DateTimeConstants.JANUARY) || // Jour de l'an
      Easter.plusDays(1).isEqual(date) || // lundi de Pâques
      (date.getDayOfMonth == 1 && date.getMonthOfYear == DateTimeConstants.MAY) || // Fête du travail
      (date.getDayOfMonth == 8 && date.getMonthOfYear == DateTimeConstants.MAY) || // Armistice 45
      Ascension.isEqual(date) ||
      Pentcôte.isEqual(date) ||
      (date.getDayOfMonth == 14 && date.getMonthOfYear == DateTimeConstants.JULY) || // Fête National
      (date.getDayOfMonth == 15 && date.getMonthOfYear == DateTimeConstants.AUGUST) || // Armistice 1945
      (date.getDayOfMonth == 1 && date.getMonthOfYear == DateTimeConstants.NOVEMBER) || // Toussaint
      (date.getDayOfMonth == 11 && date.getMonthOfYear == DateTimeConstants.NOVEMBER) || // Armistice 1918
      (date.getDayOfMonth == 25 && date.getMonthOfYear == DateTimeConstants.DECEMBER) // Noël
  }

  private lazy val Easter: LocalDate = {
    // Lundi de paques
    val year = date.getYear
    val goldNumber = year % 19
    val yearBy100 = year / 100
    val epacte = (yearBy100 - yearBy100 / 4 - (8 * yearBy100 + 13) / 25 + (19 * goldNumber) + 15) % 30
    val daysEquinoxToMoonFull = epacte - (epacte / 28) * (1 - (epacte / 28) * (29 / (epacte + 1)) * ((21 - goldNumber) / 11))
    val weekDayMoonFull = (year + year / 4 + daysEquinoxToMoonFull + 2 - yearBy100 + yearBy100 / 4) % 7
    val daysEquinoxBeforeFullMoon = daysEquinoxToMoonFull - weekDayMoonFull
    val month = 3 + (daysEquinoxBeforeFullMoon + 40) / 44
    val day = daysEquinoxBeforeFullMoon + 28 - 31 * (month / 4)
    LocalDate.now.withYear(year).withMonthOfYear(month).withDayOfMonth(day)

    LocalDate.now.withYear(year).withMonthOfYear(month).withDayOfMonth(day)
  }

  private lazy val Ascension: LocalDate = Easter.plusDays(39)

  private lazy val Pentcôte: LocalDate = Easter.plusDays(50)

}

object MonthDay {
  def apply(date: LocalDate, events: Seq[Event]): MonthDay = {
    if (events.isEmpty) EmptyDay(date) else EventsDay(date, events)
  }
}

case class EmptyDay(date: LocalDate) extends MonthDay

case class EventsDay(date: LocalDate, events: Seq[Event]) extends MonthDay

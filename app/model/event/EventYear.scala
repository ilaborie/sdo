package model.event

import java.util.Calendar
import util.{Day, Month, Year}

/**
 * Year Events
 * @param year year
 * @param months months
 */
case class EventYear(year: Year, months: List[EventMonth]) {
  override val toString = year.toString
}

object EventYear {

  def years(events: Seq[Event]): List[EventYear] = {
    // Starting Date
    val startDate = Calendar.getInstance()
    startDate.setTimeInMillis(events.map(_.from.getTimeInMillis).min)
    val startYear = startDate.get(Calendar.YEAR)

    // End Date
    val endDate = Calendar.getInstance()
    endDate.setTimeInMillis(events.map(_.to.getTimeInMillis).max)
    val endYear = endDate.get(Calendar.YEAR)

    // From min date to max date
    val years = for (y <- startYear to endYear) yield {
      val year = Year(y)
      val startMonth = if (startDate.get(Calendar.YEAR) == y) startDate.get(Calendar.MONTH) else Calendar.JANUARY
      val endMonth = if (endDate.get(Calendar.YEAR) == y) endDate.get(Calendar.MONTH) else Calendar.DECEMBER

      val months = for (m <- startMonth to endMonth) yield {
        val month = Month(m)
        val monthEvents: Seq[Event] = events.filter(_.applyTo(year, month))
        EventMonth(year, month, monthEvents)
      }
      EventYear(year, months.toList)
    }
    years.toList
  }
}

/**
 * Month Event
 */
case class EventMonth(year: Year, month: Month, weeks: List[EventWeek]) {
  override val toString = month.toString

}

object EventMonth {
  def apply(year: Year, month: Month, events: Seq[Event]): EventMonth = {
    val lastDate = Month.lastDate(year, month)

    // From create Days event
    val days = for (d <- 1 to lastDate.day) yield {
      val day = Day(d)
      val dayEvents = events.filter(_.applyTo(year, month, day))
      MonthDay(year, month, day, dayEvents)
    }

    val weeks: List[EventWeek] = {
      //  group by week
      val weeksDays: List[(Int, Seq[MonthDay])] = days.groupBy(md => Day.week(year, month, md.day))
        .toList
        .sorted(Ordering.by((x: (Int, Seq[MonthDay])) => x._1))

      // build week
      for ((w, md) <- weeksDays) yield {
        EventWeek(year, month, w, md)
      }
    }
    EventMonth(year, month, weeks)
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

  def apply(year: Year, month: Month, week: Int, events: Seq[MonthDay]): EventWeek = {
    val lst = events.sorted(Ordering.by((md: MonthDay) => md.day.day))
    if (week < 2) EventWeek(padLeft(lst.toList))
    else EventWeek(padRight(lst.toList))
  }
}

/**
 * Day Event
 */
abstract sealed class EventDay

case object Nope extends EventDay

abstract class MonthDay extends EventDay {
  def day: Day
}

object MonthDay {
  def apply(year: Year, month: Month, day: Day, events: Seq[Event]): MonthDay = {
    if (events.isEmpty) EmptyDay(day) else EventsDay(day, events)
  }
}

case class EmptyDay(day: Day) extends MonthDay

case class EventsDay(day: Day, events: Seq[Event]) extends MonthDay

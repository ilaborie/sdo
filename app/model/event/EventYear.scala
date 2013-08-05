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

        val monthEvents: Seq[Event] = events.filter(_.applyTo(ym.toInterval))
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
    val monthEnd = interval.getEnd.toLocalDate.plusDays(-1)

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
        .sorted(Ordering.by((x: (Int, Seq[MonthDay])) => x._1))
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
abstract sealed class EventDay

case object Nope extends EventDay

abstract class MonthDay extends EventDay {
  def date: LocalDate
}

object MonthDay {
  def apply(date: LocalDate, events: Seq[Event]): MonthDay = {
    if (events.isEmpty) EmptyDay(date) else EventsDay(date, events)
  }
}

case class EmptyDay(date: LocalDate) extends MonthDay

case class EventsDay(date: LocalDate, events: Seq[Event]) extends MonthDay

package model.event

import java.util.Calendar
import org.apache.commons.lang3.time.FastDateFormat

/**
 * Date Range
 */
sealed abstract class DateRange {
  def intersection(range: DateRange): DateRange

  def isEmpty: Boolean
}

object EmptyRange extends DateRange {
  def intersection(range: DateRange) = this

  val isEmpty = true
}

case class ClosedDateRange(from: Long, to: Long) extends DateRange {
  require(to > from)
  val isEmpty = false

  def intersection(range: DateRange) = range match {
    case EmptyRange => EmptyRange
    case ClosedDateRange(from2, to2) => {
      val newFrom = Math.max(from, from2)
      val newTo = Math.min(to, to2)
      if (newFrom > newTo) EmptyRange else ClosedDateRange(newFrom, newTo)
    }
  }
}

object ClosedDateRange {
  def apply(from: Calendar, to: Calendar): ClosedDateRange = ClosedDateRange(from.getTimeInMillis, to.getTimeInMillis)

  def apply(year: Year): ClosedDateRange = {
    val from = {
      val cal = Calendar.getInstance()
      cal.clear()
      cal.set(Calendar.YEAR, year.year)
      cal.set(Calendar.MONTH, Calendar.JANUARY)
      cal.set(Calendar.DATE, 1)
      cal
    }
    val to = {
      val cal = Calendar.getInstance()
      cal.clear()
      cal.set(Calendar.YEAR, year.year + 1)
      cal.set(Calendar.MONTH, Calendar.JANUARY)
      cal.set(Calendar.DATE, 1)
      cal.add(Calendar.MILLISECOND, -1)
      cal
    }
    ClosedDateRange(from, to)
  }

  def apply(year: Year, month: Month): ClosedDateRange = {
    val from = {
      val cal = Calendar.getInstance()
      cal.clear()
      cal.set(Calendar.YEAR, year.year)
      cal.set(Calendar.MONTH, month.month)
      cal.set(Calendar.DATE, 1)
      cal
    }
    val to = {
      val cal = Calendar.getInstance()
      cal.clear()
      cal.set(Calendar.YEAR, year.year)
      cal.set(Calendar.MONTH, month.month + 1)
      cal.set(Calendar.DATE, 1)
      cal.add(Calendar.MILLISECOND, -1)
      cal
    }
    ClosedDateRange(from, to)
  }

  def apply(year: Year, month: Month, day: Day): ClosedDateRange = {
    val from = {
      val cal = Calendar.getInstance()
      cal.clear()
      cal.set(Calendar.YEAR, year.year)
      cal.set(Calendar.MONTH, month.month)
      cal.set(Calendar.DATE, day.day)
      cal
    }
    val to = {
      val cal = Calendar.getInstance()
      cal.clear()
      cal.set(Calendar.YEAR, year.year)
      cal.set(Calendar.MONTH, month.month)
      cal.set(Calendar.DATE, day.day + 1)
      cal.add(Calendar.MILLISECOND, -1)
      cal
    }
    ClosedDateRange(from, to)
  }
}

/**
 * A Year
 * @param year the year
 */
case class Year(year: Int) {
  require(year >= 2013)

  override val toString = year.toString
}

/**
 * A Month
 * @param month months
 */
case class Month(month: Int) {
  // see java.util.Calendar constants (from 0 to 11)
  require(month >= 0 && month < 12)

  override val toString = {
    val cal = Calendar.getInstance()
    cal.set(Calendar.MONTH, month)
    val s = FastDateFormat.getInstance("MMMM").format(cal)
    s.charAt(0).toUpper + s.substring(1)
  }
}

object Month {

  def lastDate(year: Year, month: Month): Day = {
    val cal = Calendar.getInstance()
    cal.clear()
    cal.set(Calendar.YEAR, year.year)
    cal.set(Calendar.MONTH, month.month + 1)
    cal.set(Calendar.DATE, 1)
    cal.add(Calendar.MILLISECOND, -1)

    Day(cal.get(Calendar.DATE))
  }
}

/**
 * A Day
 * @param day the day
 */
case class Day(day: Int) {
  require(day >= 0 && day < 32)

  override val toString = day.toString
}

object Day {

  def week(year: Year, month: Month, day: Day): Int = {
    val cal = Calendar.getInstance()
    cal.clear()
    cal.set(Calendar.YEAR, year.year)
    cal.set(Calendar.MONTH, month.month)
    cal.set(Calendar.DATE, day.day)

    cal.get(Calendar.WEEK_OF_MONTH)
  }
}

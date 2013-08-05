package model

import org.joda.time.LocalDate

/**
 * Package
 */
package object event {

  implicit def dateTimeOrdering: Ordering[LocalDate] = Ordering.fromLessThan(_ isBefore _)
}

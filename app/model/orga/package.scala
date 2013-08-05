package model

import org.joda.time.LocalDate

package object orga {
  type Info = String
  type LicenseNumber = String


  implicit def dateTimeOrdering: Ordering[LocalDate] = Ordering.fromLessThan(_ isBefore _)
}

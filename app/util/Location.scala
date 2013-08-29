package util

/**
 * Location
 * @param name name
 * @param venue venue
 */
case class Location(name: String, venue: Option[String] = None, address: Option[Address] = None, tel: Option[Telephone] = None) {
  override val toString = venue match {
    case Some(v) => s"$name - $v"
    case None => name
  }
}

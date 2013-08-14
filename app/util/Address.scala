package util

/**
  */
case class Address(name: String, lines: List[String], city: String) {

  override val toString = s"""
  $name
  ${lines.mkString("\n")}
  $city """
}

object Address {
  def apply(string: String): Address = {
    val all = string.split(",").map(_.trim)
    val name = all(1)
    val city = all.last
    val lines = all.drop(1).take(all.size - 2)
    Address(name, lines.toList, city)
  }

  def apply(name: String, string: String): Address = {
    val all = string.split(",").map(_.trim)
    val city = all.last
    val lines = all.take(all.size - 1)
    Address(name, lines.toList, city)
  }
}

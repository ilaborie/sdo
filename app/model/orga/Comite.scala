package model.orga

/**
 * User: igorlaborie
 * Date: 21/07/13
 * Time: 11:19
 */
case class Comite(name: String, shortName: String, info: Option[String]) {
  lazy val fullName = s"[$shortName] $name"

  override def toString = fullName
}

object Comite {
  val ToulouseNord = Comite("Toulouse Nord", "TLS", None)
  val Toulouse = Comite("Toulouse", "NTL", None)
}

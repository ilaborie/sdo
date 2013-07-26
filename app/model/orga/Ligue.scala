package model.orga

/**
 * User: igorlaborie
 * Date: 21/07/13
 * Time: 11:19
 */
case class Ligue(name: String, shortName: String, comites: Set[Comite], info: Option[String]) {
  lazy val fullName = s"[$shortName] $name"
}

object Ligue {
  val SudOuest = Ligue("Sud Ouest", "SDO", Set(Comite.Toulouse, Comite.ToulouseNord), None)
}

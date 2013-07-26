package model.orga

/**
 * User: igorlaborie
 * Date: 21/07/13
 * Time: 11:19
 */
case class Ligue(name: String, shortName: String, comites: Seq[Comite], info: Option[Info]) {
  lazy val fullName = s"[$shortName] $name"

  override def toString = fullName
}

object Ligue {

  val all: Seq[Ligue] = List(
    Ligue("Sud Ouest", "SDO", List(Comite.Toulouse, Comite.ToulouseNord), None)
  )

  def findByShortName(shortName: String): Option[Ligue] = all.find(_.shortName == shortName)
}

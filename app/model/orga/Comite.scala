package model.orga

/**
 * User: igorlaborie
 * Date: 21/07/13
 * Time: 11:19
 */
case class Comite(name: String, shortName: String, clubs: Seq[Club], info: Option[Info]) {
  lazy val fullName = s"[$shortName] $name"

  override def toString = fullName

  def findClubByShortName(shortName: String): Option[Club] = clubs.find(_.shortName == shortName)
}

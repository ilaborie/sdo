package model.orga

/**
 * User: igorlaborie
 * Date: 21/07/13
 * Time: 11:20
 */
sealed abstract class Player extends Participant {
  override def toString = this match {
    case NotLicensedPlayer(name) => name
    case LicensedPlayer(_, name, surname, _) => if (surname.isDefined) s"$name «$surname»" else name
  }
}

case class NotLicensedPlayer(name: String) extends Player

case class LicensedPlayer(licenseNumber: LicenseNumber, name: String, surname: Option[String], club: Club) extends Player

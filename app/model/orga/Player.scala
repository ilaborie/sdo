package model.orga

/**
 * User: igorlaborie
 * Date: 21/07/13
 * Time: 11:20
 */
sealed abstract class Player extends Participant {
  override def toString = this match {
    case NotLicensedPlayer(name) => name
    case LicensedPlayer(_, name, surname) => if (surname.isDefined) s"«$surname»" else name
  }
}

case class NotLicensedPlayer(name: String) extends Player

case class LicensedPlayer(licenseNumber: LicenseNumber, name: String, surname: Option[String]) extends Player


case class Doublette(player1: Player, player2: Player) extends Participant {
  require(player1 != player2, "Deux joueurs différent dans une doublette")
}


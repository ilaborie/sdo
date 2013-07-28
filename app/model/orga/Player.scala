package model.orga

/**
 * User: igorlaborie
 * Date: 21/07/13
 * Time: 11:20
 */
sealed abstract class Player extends Participant {

  def junior: Boolean

  def feminine: Boolean
}

case class NotLicensedPlayer(name: String, junior: Boolean = false, feminine: Boolean = false) extends Player {
  override def toString = name
}

case class LicensedPlayer(licenseNumber: LicenseNumber, name: String, surname: Option[String], junior: Boolean = false, feminine: Boolean = false) extends Player {
  override def toString = surname match {
    case Some(sn) => s"«$sn»"
    case _ => name
  }
}


case class Doublette(player1: Player, player2: Player) extends Participant {
  require(player1 != player2, "Deux joueurs différent dans une doublette")
}


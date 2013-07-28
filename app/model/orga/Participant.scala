package model.orga

/**
 * Participant
 */
sealed abstract class Participant {
  def club: String
}

/**
 * Single Player
 */
sealed abstract class Player extends Participant {
  def junior: Boolean

  def feminine: Boolean
}

/**
 * Not Licensed player
 * @param name name
 * @param junior is junior
 * @param feminine is feminine
 */
case class NotLicensedPlayer(name: String, junior: Boolean = false, feminine: Boolean = false) extends Player {
  override def toString = name

  def club: String = "NL"
}


/**
 * Licensed player
 * @param licenseNumber license
 * @param name name
 * @param surname surname
 * @param junior junior
 * @param feminine feminine
 */
case class LicensedPlayer(licenseNumber: LicenseNumber,
                          name: String,
                          surname: Option[String],
                          junior: Boolean = false,
                          feminine: Boolean = false) extends Player {

  override def toString = surname match {
    case Some(sn) => s"«$sn»"
    case _ => name
  }

  def club: String = ??? // FIXME implements
}

/**
 * Doublette
 * @param player1 first player
 * @param player2 second player
 */
case class Doublette(player1: Player, player2: Player) extends Participant {
  require(player1 != player2, "Deux joueurs différent dans une doublette")

  def club: String = {
    val club1 = player1.club
    val club2 = player2.club
    if (club1 == club2) club1 else s"$club1 - $club2"
  }
}

/**
 * Team
 * @param name name
 * @param players players
 */
case class Team(name: String, players: Seq[LicensedPlayer]) extends Participant {
  def club: String = ??? // FIXME implements
}


package model.orga

/**
 * Participant
 */
sealed abstract class Participant {
  def clubAsString: String
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

  def clubAsString = "NL"
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

  def ligue: Ligue = {
    // FIXME Cache
    Ligue.ligues.find(_.players.contains(this)).get
  }

  def comite: Comite = {
    // FIXME Cache
    ligue.comites.find(_.players.contains(this)).get
  }

  def club: Club = {
    // FIXME Cache
    comite.clubs.find(_.players.contains(this)).get
  }

  def team: Team = {
    // FIXME Cache
    club.teams.find(_.players.contains(this)).get
  }


  def clubAsString = club.name
}

/**
 * Doublette
 * @param player1 first player
 * @param player2 second player
 */
case class Doublette(player1: Player, player2: Player) extends Participant {
  require(player1 != player2, "Deux joueurs différent dans une doublette")

  def clubAsString: String = {
    val club1 = player1.clubAsString
    val club2 = player2.clubAsString
    if (club1 == club2) club1 else s"$club1 - $club2"
  }
}

/**
 * Team
 * @param name name
 * @param players players
 */
case class Team(name: String, players: Seq[LicensedPlayer]) extends Participant {
  def ligue: Ligue = {
    // FIXME Cache
    Ligue.ligues.find(_.teams.contains(this)).get
  }

  def comite: Comite = {
    // FIXME Cache
    ligue.comites.find(_.teams.contains(this)).get
  }

  def club: Club = {
    // FIXME Cache
    comite.clubs.find(_.teams.contains(this)).get
  }

  def clubAsString: String = club.name
}


package model.orga

import play.api.cache.Cache
import play.api.Play.current

/**
 * Participant
 */
sealed abstract class Participant {
  def clubAsString: String

  def name: String
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

  // FIXME read NL
  def clubAsString = "NL"
}

object NotLicensedPlayer {
  // FIXME def findByName(name: String): Option[LicensedPlayer] = Ligue.nlPlayers.find(_.name == name)
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

  def ligue: Ligue = Cache.getOrElse[Ligue](s"player.$name.ligue") {
    Ligue.ligues.find(_.players.contains(this)).get
  }

  def comite: Comite = Cache.getOrElse[Comite](s"player.$name.comite") {
    ligue.comites.find(_.players.contains(this)).get
  }

  def club: Club = Cache.getOrElse[Club](s"player.$name.club") {
    comite.clubs.find(_.players.contains(this)).get
  }

  def team: Team = Cache.getOrElse[Team](s"player.$name.team") {
    club.teams.find(_.players.contains(this)).get
  }

  def clubAsString = club.name
}

object LicensedPlayer {

  def findByName(name: String): Option[LicensedPlayer] = Ligue.players.find(_.name == name)
}

/**
 * Doublette
 * @param player1 first player
 * @param player2 second player
 */
case class Doublette(player1: Player, player2: Player) extends Participant {
  require(player1 != player2, "Deux joueurs différent dans une doublette")

  val name = s"$player1 / $player2"

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
case class Team(name: String, players: Seq[LicensedPlayer], omit: Boolean = false) extends Participant {
  def ligue: Ligue = Cache.getOrElse[Ligue](s"team.$name.ligue") {
    Ligue.ligues.find(_.teams.contains(this)).get
  }

  def comite: Comite = Cache.getOrElse[Comite](s"team.$name.comite") {
    ligue.comites.find(_.teams.contains(this)).get
  }

  def club: Club = Cache.getOrElse[Club](s"team.$name.club") {
    comite.clubs.find(_.teams.contains(this)).get
  }

  def clubAsString: String = club.name
}


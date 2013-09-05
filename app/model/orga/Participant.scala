package model.orga

import play.api.cache.Cache
import play.api.Play.current
import scala.Predef._
import scala.Some
import util.EMail

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
sealed trait Player extends Participant {
  def junior: Boolean

  def feminine: Boolean
}

/**
 * Not Licensed player
 * @param name name
 * @param junior is junior
 * @param feminine is feminine
 */
case class NotLicensedPlayer(name: String,
                             junior: Boolean = false,
                             feminine: Boolean = false,
                             emails: Set[EMail] = Set(),
                             twitter: Option[String] = None,
                             facebook: Option[String] = None,
                             google: Option[String] = None) extends Player {
  override def toString = name

  def clubAsString = "NL"
}

object NotLicensedPlayer {
  def findByName(name: String): Option[NotLicensedPlayer] = Ligue.nlPlayers.find(_.name == name)
}

/**
 * Team
 */
sealed abstract class TeamParticipant extends Participant {
  def club: Club
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
                          surname: Option[String]=None,
                          junior: Boolean = false,
                          feminine: Boolean = false,
                          emails: Set[EMail] = Set(),
                          twitter: Option[String] = None,
                          facebook: Option[String] = None,
                          google: Option[String] = None) extends TeamParticipant with Player {

  override def toString = surname match {
    case Some(sn) => s"«$sn»"
    case _ => name
  }

  lazy val ligue: Ligue = Ligue.ligues.find(_.players.contains(this)).get

  lazy val comite: Comite = ligue.comites.find(_.players.contains(this)).get

  lazy val club: Club = comite.clubs.find(_.players.contains(this)).get

  lazy val team: Team = club.teams.find(_.players.contains(this)).get

  def clubAsString = club.name
}

object LicensedPlayer {

  def findByName(name: String): Option[LicensedPlayer] = Ligue.players.find(_.name == name)
}

/**
 * Team Doublette
 * @param player1 first player
 * @param player2 second player
 */
case class TeamDoublette(player1: LicensedPlayer, player2: LicensedPlayer) extends TeamParticipant {
  require(player1 != player2, "Deux joueurs différent dans une doublette")
  require(player1.club == player2.club, "Deux joureurs dans le même club")

  override val toString = name
  val name = s"${player1.name} / ${player2.name}"
  val club = player1.club

  val clubAsString = club.name
}

/**
 * Doublette
 * @param player1 first player
 * @param player2 second player
 */
case class Doublette(player1: Player, player2: Player) extends Participant {
  require(player1 != player2, "Deux joueurs différent dans une doublette")

  val name = s"${player1.name} / ${player2.name}"

  def clubAsString: String = {
    val club1 = player1.clubAsString
    val club2 = player2.clubAsString
    if (club1 == club2) club1 else s"$club1 - $club2"
  }
}


// The MIT License (MIT)
//
// Copyright (c) 2013 Igor Laborie
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of
// this software and associated documentation files (the "Software"), to deal in
// the Software without restriction, including without limitation the rights to
// use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
// the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
// IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
// CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package model.orga

import util.EMail

/**
 * Participant
 */
sealed abstract class Participant {
  def clubAsString: String

  def name: String

  def contains(other: Player) = this == other
}

/**
 * Single Player
 */
sealed trait Player extends Participant {
  def youth: Boolean

  def lady: Boolean

  lazy val men: Boolean = !lady

  def clubAsString:String

  def licenseNumber: String
  def firstName: String
  def lastName: String
  def category: String = if (lady) "F" else "M"
}

/**
 * Not Licensed player
 * @param youth is youth
 * @param lady is ladies
 */
case class NotLicensedPlayer(override val firstName: String,
                             override val lastName: String,
                             youth: Boolean = false,
                             lady: Boolean = false,
                             emails: Set[EMail] = Set(),
                             twitter: Option[String] = None,
                             facebook: Option[String] = None,
                             google: Option[String] = None) extends Player {
  val name = s"$lastName $firstName"
  override def toString = name

  def clubAsString = "NL"
  def licenseNumber = ""
}

object NotLicensedPlayer {
  def findByName(name: String): Option[NotLicensedPlayer] = Ligue.nlPlayers.find(_.name == name)
}

/**
 * Team
 */
sealed trait TeamParticipant extends Participant {
  def club: Club
}


/**
 * Licensed player
 * @param licenseNumber license
 * @param firstName first name
 * @param lastName last name
 * @param surname surname
 * @param youth youth
 * @param lady ladies
 */
case class LicensedPlayer(override val licenseNumber: LicenseNumber,
                          override val firstName: String,
                          override val lastName: String,
                          commonName: Option[String] = None,
                          surname: Option[String] = None,
                          youth: Boolean = false,
                          lady: Boolean = false,
                          emails: Set[EMail] = Set(),
                          twitter: Option[String] = None,
                          facebook: Option[String] = None,
                          google: Option[String] = None) extends TeamParticipant with Player {

  val name = s"$lastName $firstName"
  override val toString = name

  lazy val ligue: Ligue = Ligue.ligues.find(_.players.contains(this)).get

  lazy val comite: Comite = ligue.comites.find(_.players.contains(this)).get

  lazy val club: Club = comite.clubs.find(_.players.contains(this)).get

  lazy val team: Team = club.teams.find(_.players.contains(this)).get

  def clubAsString = club.shortName
}

object LicensedPlayer {

  def findByName(name: String): Option[LicensedPlayer] = Ligue.players.find(_.name == name)
}

/**
 * Team Doublette
 * @param player1 first player
 * @param player2 second player
 */
case class TeamPair(player1: LicensedPlayer, player2: LicensedPlayer) extends TeamParticipant {
  require(player1 != player2, "Two different player")
  require(player1.club == player2.club, "Same club")

  override def contains(player: Player): Boolean = (player1 == player) || (player2 == player)

  def other(player: Player): Player = {
    require(contains(player))
    if (player1 == player) player2 else player1
  }


  val name = s"${player1.name} / ${player2.name}"
  override val toString = name
  val club = player1.club

  val clubAsString = club.shortName
}

/**
 * Doublette
 * @param player1 first player
 * @param player2 second player
 */
case class Pair(player1: Player, player2: Player) extends Participant {
  require(player1 != player2, "Two different player")

  val name = s"${player1.name} / ${player2.name}"

  override val toString = name

  override def contains(player: Player): Boolean = (player1 == player) || (player2 == player)

  def other(player: Player): Player = {
    require(contains(player))
    if (player1 == player) player2 else player1
  }

  def clubAsString: String = {
    val club1 = player1.clubAsString
    val club2 = player2.clubAsString
    if (club1 == club2) club1 else s"$club1 / $club2"
  }
}


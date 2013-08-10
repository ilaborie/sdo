package model.user

import securesocial.core._
import model.orga.LicensedPlayer

/**
 * A user
 */
sealed abstract class User {
  def id: Identity
}

case class Guest(id: Identity) extends User

case class LocalUser(id: Identity, player: LicensedPlayer) extends User

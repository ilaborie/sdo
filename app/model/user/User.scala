package model.user

import play.api.cache.Cache
import play.api.Play.current

import securesocial.core._

import model.orga._
import util._

/**
 * A user
 */
sealed abstract class User {
  def id: Identity
}

object User {

  /**
   * Return user from identity
   * @param id identity
   * @return user
   */
  def apply(id: Identity): User = Cache.getOrElse[User](id.identityId.userId + "|" + id.identityId.providerId) {
    val userId: String = id.identityId.userId
    val user = id.authMethod.method match {
      case "twitter" => findUserBy(id) {
        player => player.twitter == Some(userId)
      }
      case "facebook" => findUserBy(id) {
        player => player.facebook == Some(userId)
      }
      case "google" => findUserBy(id) {
        player => player.google == Some(userId)
      }
      case _ => findUserByEmail(id)
    }

    user match {
      case Guest(_) => sendMailForGuest(id)
      case _ => // Nothing to do
    }

    user
  }

  private def sendMailForGuest(id: Identity) {
    val body = emails.html.newUser(id)
    Mailer.sendEmail("[SDO] newUser", "ilaborie@gmail.com", body)
  }

  private def findUserBy(id: Identity)(filter: (LicensedPlayer) => Boolean): User = Ligue.players.find(filter) match {
    case Some(player) => LocalUser(id, player)
    case None => if (id.email.isEmpty) Guest(id) else findUserByEmail(id)
  }

  private def findUserByEmail(id: Identity): User = {
    id.email match {
      case None => Guest(id)
      case Some(email) =>
        val search: Option[LicensedPlayer] = Ligue.players.find(_.emails.contains(EMail(email)))
        search match {
          case Some(player) => LocalUser(id, player)
          case None => Guest(id)
        }
    }
  }

}

case class Guest(id: Identity) extends User

case class LocalUser(id: Identity, player: LicensedPlayer) extends User

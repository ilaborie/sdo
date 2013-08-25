package model.user

import play.api.Logger
import play.api.cache.Cache
import play.api.Play.current
import play.libs.Akka

import securesocial.core._

import model.orga.{Ligue, LicensedPlayer}
import util.EMail

/**
 * A user
 */
sealed abstract class User {
  def id: Identity
}

object User {

  private val logger = Logger("user")

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
    import com.typesafe.plugin._
    import scala.concurrent.duration._
    import play.api.libs.concurrent.Execution.Implicits._

    // Send a mail
    Akka.system.scheduler.scheduleOnce(1 seconds) {
      val mail = use[MailerPlugin].email
      mail.setSubject("[SDO] newUser")
      mail.addRecipient("ilaborie@gmail.com")
      mail.addFrom("ilaborie@gmail.com")
      // the mailer plugin handles null / empty string gracefully

      val body = s"""
      User Id: ${id.identityId.userId}
      Fist name: ${id.firstName}
      Last name: ${id.lastName}
      Full name: ${id.fullName}
      Logged in from: ${id.identityId.providerId}
      Email: ${id.email.getOrElse("")}
      Authentication method: ${id.authMethod.method}
      """
      mail.send(body, "")
    }
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

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

package model.user

import play.api.cache.Cache
import play.api.Play.current

import securesocial.core._

import model.orga._
import util._
import akka.actor.Cancellable
import play.api.mvc.AnyContent

/**
 * A user
 */
sealed abstract class User {
  def id: Identity

  def currentPlayer:Option[Player] = None

  val isAdmin = false
}

object User {

  /**
   * Return user from identity
   * @param id identity
   * @return user
   */
  def apply(id: Identity): User = Cache.getOrElse[User](id.userIdFromProvider.authId + "|" + id.userIdFromProvider.providerId) {
    val userId: String = id.userIdFromProvider.authId
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

  def apply(request: SecuredRequest[AnyContent]): User = apply(request.user)

  private def sendMailForGuest(id: Identity): Cancellable = {
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

case class LocalUser(id: Identity, player: LicensedPlayer) extends User {
  override val currentPlayer = Some(player)
  override val isAdmin = player.emails.contains(EMail("ilaborie@gmail.com"))
}


package service

import play.api.{Logger, Application}
import securesocial.core._
import securesocial.core.IdentityId
import securesocial.core.providers.Token


/**
 * A Sample In Memory user service in Scala
 *
 * IMPORTANT: This is just a sample and not suitable for a production environment since
 * it stores everything in memory.
 */
class UserService(application: Application) extends UserServicePlugin(application) {

  private val logger = Logger("UserService")

  private var users = Map[String, Identity]()
  private var tokens = Map[String, Token]()

  def find(id: IdentityId): Option[Identity] = {
    logger.trace(s"find($id) with users = $users")
    users.get(id.userId + id.providerId)
  }


  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    def checkUser(user: Identity) = {
      user.email.exists(_ == email) && user.identityId.providerId == providerId
    }
    logger.trace(s"findByEmailAndProvider($email, $providerId) with users = $users")
    users.values.find(checkUser)
  }

  def save(user: Identity): Identity = {
    logger.trace(s"save($users)")
    users = users + (user.identityId.userId + user.identityId.providerId -> user)
    // this sample returns the same user object, but you could return an instance of your own class
    // here as long as it implements the Identity trait. This will allow you to use your own class in the protected
    // actions and event callbacks. The same goes for the find(id: UserId) method.
    user
  }

  def save(token: Token) {
    logger.trace(s"save($token)")
    tokens += (token.uuid -> token)
  }

  def findToken(token: String): Option[Token] = {
    logger.trace(s"findToken($token)")
    tokens.get(token)
  }

  def deleteToken(uuid: String) {
    logger.trace(s"deleteToken($uuid)")
    tokens -= uuid
  }

  def deleteTokens() {
    logger.trace(s"deleteTokens()")
    tokens = Map()
  }

  def deleteExpiredTokens() {
    logger.trace(s"deleteExpiredTokens()")
    tokens = tokens.filter(!_._2.isExpired)
  }
}


package service

import play.api.{Logger, Application}
import securesocial.core._
import securesocial.core.IdentityId
import securesocial.core.providers.Token

import reactivemongo.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

import reactivemongo.api.collections.default.BSONCollection
import reactivemongo.bson.BSONDocument
import scala.concurrent.{Await, Future}
import org.joda.time.DateTime

/**
 * Store User into a MongoDB instance
 */
class UserService(application: Application) extends UserServicePlugin(application) {

  private val logger = Logger("UserService")

  lazy val connection = {
    val driver = new MongoDriver
    val host = application.configuration.getString("mongodb.servers").getOrElse("localhost")
    val connect = driver.connection(List(host))

    val db = application.configuration.getString("mongodb.db")
    db match {
      case Some(database) => connect(database)
      case None => throw new DatabaseException("No 'mongodb.db' found into configuration")
    }
  }
  lazy val tokens: BSONCollection = connection("tokens")
  lazy val users: BSONCollection = connection("users")

  // Users

  private def toIdentity(document: BSONDocument): Identity = {
    val userId = document.getAs[String]("userId").get
    val providerId = document.getAs[String]("providerId").get

    val id: IdentityId = IdentityId(userId, providerId)
    val firstName: String = document.getAs[String]("firstName").get
    val lastName: String = document.getAs[String]("lastName").get
    val email: Option[String] = document.getAs[String]("email")
    val avatarUrl: Option[String] = document.getAs[String]("avatarUrl")
    val authMethod: AuthenticationMethod = AuthenticationMethod(document.getAs[String]("method").get)

    val fullName: String = s"$lastName $firstName"

    val passwordDoc: BSONDocument = document.getAs[BSONDocument]("passwordInfo").get
    val hasher = passwordDoc.getAs[String]("hasher").get
    val password = passwordDoc.getAs[String]("password").get
    val salt = passwordDoc.getAs[String]("salt")
    val passwordInfo = PasswordInfo(hasher, password, salt)

    LocalUser(id, firstName, lastName, fullName, email, avatarUrl, authMethod, Some(passwordInfo))
  }

  def find(id: IdentityId): Option[Identity] = {
    logger.trace(s"find($id)")

    val query = BSONDocument(
      "userId" -> id.userId,
      "providerId" -> id.providerId
    )
    val result = users.find(query).one

    val res = Await.result(result, 1 seconds)
    res map toIdentity
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    logger.trace(s"findByEmailAndProvider($email, $providerId)")
    val query = BSONDocument(
      "email" -> email,
      "providerId" -> providerId
    )
    val result = users.find(query).one

    val res = Await.result(result, 1 seconds)
    res map toIdentity
  }

  def createId(id: IdentityId) = s"${id.userId}|${id.providerId}"

  def save(user: Identity): Identity = {
    logger.trace(s"save($user)")
    val passwordInfo = user.passwordInfo.get
    val doc = BSONDocument(
      "_id" -> createId(user.identityId),
      "userId" -> user.identityId.userId,
      "providerId" -> user.identityId.providerId,
      "method" -> user.authMethod.method,
      "avatarUrl" -> user.avatarUrl,
      "email" -> user.email,
      "firstName" -> user.firstName,
      "lastName" -> user.lastName,
      "passwordInfo" -> BSONDocument(
        "hasher" -> passwordInfo.hasher,
        "password" -> passwordInfo.password,
        "salt" -> passwordInfo.salt
      )
    )
    users.save(doc)
    user
  }

  // Tokens
  private def toToken(document: BSONDocument): Token = {
    val uuid = document.getAs[String]("uuid").get
    val email = document.getAs[String]("email").get
    val creationTime: DateTime = new DateTime(document.getAs[Long]("creationTime").get)
    val expirationTime: DateTime = new DateTime(document.getAs[Long]("expirationTime").get)
    val isSignUp = document.getAs[Boolean]("isSignUp").get
    Token(uuid, email, creationTime, expirationTime, isSignUp)
  }

  def save(token: Token) {
    logger.trace(s"save($token)")
    val doc = BSONDocument(
      "uuid" -> token.uuid,
      "email" -> token.email,
      "creationTime" -> token.expirationTime.getMillis,
      "expirationTime" -> token.expirationTime.getMillis,
      "isSignUp" -> token.isSignUp
    )
    tokens.save(doc)
  }

  def findToken(token: String): Option[Token] = {
    logger.trace(s"findToken($token)")

    val query = BSONDocument("uuid" -> token)
    val result: Future[Option[BSONDocument]] = tokens.find(query).one[BSONDocument]

    val res = Await.result(result, 1 seconds)
    res map toToken
  }

  def deleteToken(uuid: String) {
    logger.trace(s"deleteToken($uuid)")
    tokens remove BSONDocument("uuid" -> uuid)
  }

  def deleteTokens() {
    logger.trace(s"deleteTokens()")
    tokens remove BSONDocument()
  }

  def deleteExpiredTokens() {
    logger.trace(s"deleteExpiredTokens()")
    val expirationTime = DateTime.now().getMillis
    val query = BSONDocument(
      "expirationTime" ->
        BSONDocument("$gt" -> expirationTime)
    )
    tokens remove query
  }
}

case class LocalUser(identityId: IdentityId,
                     firstName: String,
                     lastName: String,
                     fullName: String,
                     email: Option[String],
                     avatarUrl: Option[String],
                     authMethod: AuthenticationMethod,
                     passwordInfo: Option[PasswordInfo]) extends Identity {
  val oAuth1Info = None
  val oAuth2Info = None
}

case class DatabaseException(message: String) extends RuntimeException(message)


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

package service

import play.api.{Logger, Application}
import securesocial.core._
import securesocial.core.UserIdFromProvider
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

    val id = UserIdFromProvider(userId, providerId)
    val firstName: String = document.getAs[String]("firstName").get
    val lastName: String = document.getAs[String]("lastName").get
    val email: Option[String] = document.getAs[String]("email")
    val avatarUrl: Option[String] = document.getAs[String]("avatarUrl")
    val authMethod: AuthenticationMethod = AuthenticationMethod(document.getAs[String]("method").get)
    val fullName: String = s"$lastName $firstName"

    // Password Info
    val passwordInfo = document.getAs[BSONDocument]("passwordInfo") map {
      passwordDoc =>
        val hasher = passwordDoc.getAs[String]("hasher").get
        val password = passwordDoc.getAs[String]("password").get
        val salt = passwordDoc.getAs[String]("salt")
        PasswordInfo(hasher, password, salt)
    }

    // oAuth1Info
    val oAuth1Info = document.getAs[BSONDocument]("oAuth1Info") map {
      info =>
        val token = info.getAs[String]("token").get
        val secret = info.getAs[String]("secret").get
        OAuth1Info(token, secret)
    }

    // oAuth2Info
    val oAuth2Info = document.getAs[BSONDocument]("oAuth2Info") map {
      info =>
        val accessToken = info.getAs[String]("accessToken").get
        val expiresIn = info.getAs[Int]("expiresIn")
        val refreshToken = info.getAs[String]("refreshToken")
        val tokenType = info.getAs[String]("tokenType")
        OAuth2Info(accessToken, tokenType, expiresIn, refreshToken)
    }

    SocialUser(id, firstName, lastName, fullName, email, avatarUrl, authMethod,
      oAuth1Info = oAuth1Info,
      oAuth2Info = oAuth2Info,
      passwordInfo = passwordInfo)
  }

  def find(id: UserIdFromProvider): Option[Identity] = {
    logger.trace(s"find($id)")
    val query = BSONDocument(
      "userId" -> id.authId,
      "providerId" -> id.providerId
    )
    val result = users.find(query).one
    val res = Await.result(result, 1.seconds)
    res map toIdentity
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[Identity] = {
    logger.trace(s"findByEmailAndProvider($email, $providerId)")
    val query = BSONDocument(
      "email" -> email,
      "providerId" -> providerId
    )
    val result = users.find(query).one

    val res = Await.result(result, 1.seconds)
    res map toIdentity
  }

  def createId(id: UserIdFromProvider) = s"${id.authId}|${id.providerId}"

  def save(user: Identity): Identity = {
    logger.trace(s"save($user)")
    val doc = BSONDocument(
      "_id" -> createId(user.userIdFromProvider),
      "userId" -> user.userIdFromProvider.authId,
      "providerId" -> user.userIdFromProvider.providerId,
      "method" -> user.authMethod.method,
      "avatarUrl" -> user.avatarUrl,
      "email" -> user.email,
      "firstName" -> user.firstName,
      "lastName" -> user.lastName,
      "passwordInfo" -> user.passwordInfo.map(
        passwordInfo => BSONDocument(
          "hasher" -> passwordInfo.hasher,
          "password" -> passwordInfo.password,
          "salt" -> passwordInfo.salt)),
      "oAuth1Info" -> user.oAuth1Info.map(
        info => BSONDocument(
          "secret" -> info.secret,
          "token" -> info.token
        )),
      "oAuth2Info" -> user.oAuth2Info.map(
        info => BSONDocument(
          "accessToken" -> info.accessToken,
          "expiresIn" -> info.expiresIn,
          "refreshToken" -> info.refreshToken,
          "tokenType" -> info.tokenType
        ))
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
    ()
  }

  def findToken(token: String): Option[Token] = {
    logger.trace(s"findToken($token)")

    val query = BSONDocument("uuid" -> token)
    val result: Future[Option[BSONDocument]] = tokens.find(query).one[BSONDocument]

    val res = Await.result(result, 1.seconds)
    res map toToken
  }

  def deleteToken(uuid: String) = {
    logger.trace(s"deleteToken($uuid)")
    tokens remove BSONDocument("uuid" -> uuid)
    ()
  }

  def deleteTokens() {
    logger.trace(s"deleteTokens()")
    tokens remove BSONDocument()
    ()
  }

  def deleteExpiredTokens() {
    logger.trace(s"deleteExpiredTokens()")
    val expirationTime = DateTime.now().getMillis
    val query = BSONDocument(
      "expirationTime" ->
        BSONDocument("$gt" -> expirationTime)
    )
    tokens remove query
    ()
  }
}



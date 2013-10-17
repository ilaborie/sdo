
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

import reactivemongo.api._

import scala.concurrent.ExecutionContext.Implicits.global

import reactivemongo.api.collections.default.BSONCollection

/**
 * Store Registered player into a MongoDB instance
 */
class TournamentService(application: Application) {

  private val logger = Logger("TournamentService")

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
  lazy val registration: BSONCollection = connection("registration")

}



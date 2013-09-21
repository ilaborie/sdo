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

package model.contact

import model.orga._
import play.api.Logger
import java.util.{List => JavaList, Map => JavaMap}

import scala.collection.JavaConversions._
import util.YamlParser

/**
 * Contact reader
 */
object DataContact {
  private val logger = Logger("data")

  def readContacts(season: Season): Seq[Contact] = {
    val contactsFile = s"s$season/contacts.yml"
    logger.info(s"Read contacts information in $contactsFile")

    val contactsList = YamlParser.parseFile(contactsFile).asInstanceOf[JavaList[JavaMap[String, String]]]
    logger.trace(s"Read $contactsList")

    for (contact <- contactsList.toList) yield readContact(season, contact.toMap)
  }

  def readContact(season: Season, data: Map[String, String]): Contact = {
    val name = data("name").asInstanceOf[String]
    val email = toOption(data, "emails") match {
      case Some(em) => Some(util.EMail(em))
      case None => None
    }
    val url = toOption(data, "url")
    val information = toOption(data, "info") match {
      case Some(info) => YamlParser.readInfo(s"s$season/$info")
      case None => None
    }

    Contact(name, email, url, information)
  }

  private def toOption(data: Map[String, String], key: String): Option[String] = {
    if (!data.contains(key)) None
    else {
      val value = data(key)
      if (value != null) Some(value) else None
    }
  }
}

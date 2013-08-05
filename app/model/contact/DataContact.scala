package model.contact

import model.orga._
import play.api.Logger
import java.util.{List => JavaList, Map => JavaMap}

import scala.collection.JavaConversions._
import scala.Predef._
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
    val email = toOption(data, "email") match {
      case Some(em) => Some(EMail(em))
      case None => None
    }
    val url = toOption(data, "url")
    val information = toOption(data, "info") match {
      case Some(info) => Data.readInfo(s"data/s$season/$info")
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

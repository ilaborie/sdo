package model.contact

import model.orga._
import play.api.Logger
import util.{EMail, YamlParser}

/**
 * Contact reader
 */
object DataContact {
  private val logger = Logger("data")

  def readContacts(season: Season): Seq[Contact] = {
    val contactsFile = s"data/s$season/contacts.yml"
    logger.info(s"Read contacts information in $contactsFile")

    YamlParser.parseFile(contactsFile) match {
      case Some(contactsList) => {
        logger.trace(s"Read $contactsList")
        for (contact <- contactsList.asInstanceOf[List[Map[String, String]]])
        yield readContact(season, contact)
      }
      case None => Nil
    }
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

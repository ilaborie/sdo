package model.event

import java.util.{List => JavaList, Map => JavaMap}
import scala.collection.JavaConversions._
import play.api.Logger

import model.orga._
import util._

/**
 * Event reader
 */
object DataEvent {
  private val logger = Logger("data")

  def readEvents(season: Season): Seq[Event] = {
    val contactsFile = s"s$season/events.yml"
    logger.info(s"Read events information in $contactsFile")

    val contactsList = YamlParser.parseFile(contactsFile).asInstanceOf[JavaList[JavaMap[String, Any]]]
    logger.trace(s"Read $contactsList")

    for (contact <- contactsList.toList) yield readEvent(season, contact.toMap)
  }

  /**
   * Read an event
   * @param season the season
   * @param data the event data
   * @return the event
   */
  def readEvent(season: Season, data: Map[String, Any]): Event = {
    val name = data("name").asInstanceOf[String]
    val eventType = data("type").asInstanceOf[String]
    val from = YamlParser.readDate(data("from").asInstanceOf[String])
    val to = YamlParser.readDate(data("to").asInstanceOf[String])
    val location = {
      if (!data.contains("location")) None
      else YamlParser.readLocation(data)
    }
    val email = YamlParser.toOption(data, "emails") match {
      case Some(em) => Some(EMail(em))
      case None => None
    }
    val url = YamlParser.toOption(data, "url")
    val information = YamlParser.toOption(data, "info") match {
      case Some(info) => YamlParser.readInfo(s"s$season/$info")
      case None => None
    }

    Event(name, EventType(eventType), from, to, location, email, url, information)
  }
}

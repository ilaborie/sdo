package model.event

import model.orga._
import play.api.Logger
import util.{YamlParser, EMail}

/**
 * Event reader
 */
object DataEvent {
  private val logger = Logger("data")

  def readEvents(season: Season): Seq[Event] = {
    val eventsFile = s"data/s$season/events.yml"
    logger.info(s"Read events information in $eventsFile")

    YamlParser.parseFile(eventsFile) match {
      case Some(eventsList) => {
        logger.trace(s"Read $eventsList")
        for (contact <- eventsList.asInstanceOf[List[Map[String, Any]]])
        yield readEvent(season, contact.toMap)
      }
      case None => Nil
    }
  }

  /**
   * Read an event
   * @param season the season
   * @param data the event data
   * @return the event
   */
  def readEvent(season: Season, data: Map[String, Any]): Event = {
    println(s"read event: $data")
    println(s"read event: ${data.keySet}")
    val name = data("name").asInstanceOf[String]
    val eventType = data("type").asInstanceOf[String]
    val from = Data.readDate(data("from").asInstanceOf[String])
    val to = Data.readDate(data("to").asInstanceOf[String])
    val location = {
      if (!data.contains("location")) None
      else readLocation(data("location").asInstanceOf[Map[String, String]].toMap)
    }
    val email = toOption(data, "email") match {
      case Some(em) => Some(EMail(em))
      case None => None
    }
    val url = toOption(data, "url")
    val information = toOption(data, "info") match {
      case Some(info) => Data.readInfo(s"data/s$season/$info")
      case None => None
    }

    Event(name, EventType(eventType), from, to, location, email, url, information)
  }

  /**
   * Read Location
   * @param map data
   * @return the location or none
   */
  def readLocation(map: Map[String, String]): Option[Location] = {
    toOption(map, "name") match {
      case Some(name) => Some(Location(name, toOption(map, "venue")))
      case None => None
    }
  }

  /**
   * Retunr an optional string
   * @param data the map
   * @param key the key
   * @return the option
   */
  private def toOption(data: Map[String, Any], key: String): Option[String] = {
    if (!data.contains(key)) None
    else {
      val value = data(key)
      if (value != null) Some(value.asInstanceOf[String]) else None
    }
  }
}

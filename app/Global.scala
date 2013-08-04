import com.google.common.base.Charsets
import com.google.common.io.CharStreams
import java.io.InputStreamReader
import play.api._
import play.api.libs.Files

import model.contact.Contact
import model.event.Event
import model.orga.{Ligue, Season}
import model.team.TeamChampionship

import util.YamlParser

import play.api._

object Global extends GlobalSettings {

  private val season: Season = Season.currentSeason

  private val logger = Logger("loading")

  override def onStart(app: Application) {
    //val liguesFile = s"data/s$season/SDO/teamChampionship/d8/x1-ft.yml"
    val liguesFile = s"data/s$season/ligues.yml"
    // Check Stream
    val stream = app.resourceAsStream(liguesFile)
    if (stream.isEmpty) logger.error(s"Fail to load $liguesFile")
    else logger.info(s"OK to read $liguesFile")

    // Test another parser
    val file = app.getFile(liguesFile)
    val data = {
      val reader = new InputStreamReader(stream.get, Charsets.UTF_8)
      CharStreams.toString(reader)
    }
    logger.trace(s"$liguesFile content: $data")
    val parse = YamlParser.parse(data)
    logger.debug(s"parsed: $parse")


    // Load and trace
    logger.info("Application")
    if (logger.isTraceEnabled) {
      logger.trace("Loading ...")
      Ligue.ligues.foreach(ligue => logger.trace(s"Ligue: $ligue"))
      Event.events.foreach(event => logger.trace(s"Event: $event"))
      Contact.contacts.foreach(contact => logger.trace(s"Contact: $contact"))
      Ligue.ligues.map {
        ligue =>
          val champ = TeamChampionship(season, ligue)
          logger.trace(s"TeamChampionship: $champ")
      }
      logger.trace("[Done]")
    }
  }
}

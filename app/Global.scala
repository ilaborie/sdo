import play.api._

import model.contact.Contact
import model.event.Event
import model.orga.{Ligue, Season}
import model.team.TeamChampionship
import util.YamlParser


object Global extends GlobalSettings {

  private val season: Season = Season.currentSeason

  private val logger = Logger("loading")

  override def onStart(app: Application) {
    // Load and trace
    YamlParser.parser = YamlParser(app)
    if (logger.isInfoEnabled) {
      logger.info("Loading ...")
      Ligue.ligues.foreach(ligue => logger.info(s"Ligue: $ligue"))
      Event.events.foreach(event => logger.info(s"Event: $event"))
      Contact.contacts.foreach(contact => logger.info(s"Contact: $contact"))
      Ligue.ligues.map {
        ligue =>
          val champ = TeamChampionship(season, ligue)
          logger.info(s"TeamChampionship: $champ")
      }
      logger.info("[Done]")
    }
  }
}

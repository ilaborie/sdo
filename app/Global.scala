
import model.contact.Contact
import model.event.Event
import model.orga.{Ligue, Season}
import model.team.TeamChampionship


import play.api._

object Global extends GlobalSettings {

  private val season: Season = Season.currentSeason

  private val logger = Logger("loading")

  override def onStart(app: Application) {
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

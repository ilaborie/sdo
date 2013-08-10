import play.api._

import model.contact.Contact
import model.event.Event
import model.orga.{Ligue, Season}
import model.team.TeamChampionship
import securesocial.core.providers.utils.GravatarHelper
import securesocial.core._
import util.YamlParser


object Global extends GlobalSettings {

  private val season: Season = Season.currentSeason

  private val logger = Logger("loading")

  override def onStart(app: Application) {
    // Load Yaml data
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

    // Info for authentication
    val confKey = List(
      "securesocial.facebook.clientId",
      "securesocial.facebook.clientSecret",
      "securesocial.google.clientId",
      "securesocial.google.clientSecret")
    confKey.foreach(key => logger.warn( s"""$key: ${app.configuration.getString(key)}"""))

    if (app.mode == Mode.Dev) {
      registerUser("Igor", "Laborie", "ilaborie@gmail.com")
      registerUser("Paulo", "", "paulo@gmail.com")
    }
  }

  private def registerUser(firstName: String, lastName: String, email: String) = {
    val identityId = IdentityId(email, "userpass")
    val password = "plop"
    val user = SocialUser(
      identityId,
      firstName,
      lastName,
      s"$firstName $lastName",
      Some(email),
      GravatarHelper.avatarFor(email),
      AuthenticationMethod.UserPassword,
      passwordInfo = Some(Registry.hashers.currentHasher.hash(password))
    )
    UserService.save(user)
  }
}

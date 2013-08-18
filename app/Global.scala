
import play.api.{Application, Logger, GlobalSettings, Mode}
import play.api.mvc._

import securesocial.core.providers.utils.GravatarHelper
import securesocial.core._

import util.YamlParser
import model.contact.Contact
import model.event.Event
import model.orga.{Ligue, Season}
import model.team.TeamChampionship


object Global extends GlobalSettings {

  private val season: Season = Season.currentSeason

  private val logger = Logger("loading")

  private val invalidAction = Action {
    request => Results.Ok(views.html.invalid())
  }

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

  /**
   * Filter invalid browser
   * @param action the action
   * @return the action or an invalid action
   */
  override def doFilter(action: EssentialAction): EssentialAction = new EssentialAction {
    def apply(request: RequestHeader) = {
      val accept = request.headers("Accept")
      if (!accept.contains("text/html")) action.apply(request)
      else {
        val userAgent = request.headers("user-agent")
        val obsolete = for (version <- 6 to 9) yield s"msie $version."
        if (obsolete.filter(_.contains(userAgent)).isEmpty) action.apply(request)
        else invalidAction.apply(request)
      }
    }
  }

  private def registerUser(firstName: String, lastName: String, email: String): Identity = {
    val identityId = IdentityId(email, "userpass")
    UserService.find(identityId) match {
      case Some(u) => u
      case None =>
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

  override def onStop(app: Application) = {
    // FIXME Close Mongo Connection
    // UserService.delegate
  }

}

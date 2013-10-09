
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
      val sdo = Ligue.ligues(0)
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
    }
    ()
  }

  /**
   * Filter invalid browser
   * @param action the action
   * @return the action or an invalid action
   */
  override def doFilter(action: EssentialAction): EssentialAction = new EssentialAction {
    def apply(request: RequestHeader) = {
      val accept = request.headers("Accept")
      val userAgent = request.headers("user-agent")
      if (!accept.contains("text/html")) action.apply(request)
      else {
        // Reject IE < 10
        logger.trace(s"uri: ${request.uri}, accept: $accept, user-agent: $userAgent")
        val obsolete = for (version <- 6 to 9) yield s"MSIE $version."
        if (obsolete.filter(userAgent.contains(_)).isEmpty) action.apply(request)
        else invalidAction.apply(request)
      }
    }
  }

  private def registerUser(firstName: String, lastName: String, email: String): Identity = {
    val identityId = UserIdFromProvider(email, "userpass")
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
    // FIXME maybe Close Mongo Connection
  }

}

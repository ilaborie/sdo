package controllers

import play.api.mvc._
import model.orga._
import model.contact.Contact
import model.event.{EventYear, Event}

import securesocial.core.{Identity, Authorization}

/**
 * Mains pages
 */
object Application extends Controller with securesocial.core.SecureSocial {

  private val season: Season = Season.currentSeason

  /**
   * Index page
   * @return the index page
   */
  def index = ligues

  /**
   * Ligues Pages
   * @return ligues page
   */
  def ligues = SecuredAction { implicit request =>
    Ok(views.html.ligues(request.user, Ligue.ligues))
  }

  /**
   * Ligue page
   * @param shortName the ligue short name
   * @return the ligue page
   */
  def ligue(shortName: String) = Action {
    LigueAction(shortName) {
      ligue => Ok(views.html.ligue.ligue(ligue))
    }.result
  }

  /**
   * Ligue body page
   * @param shortName the ligue short name
   * @return the ligue page
   */
  def ligueBody(shortName: String) = Action {
    LigueAction(shortName) {
      ligue => Ok(views.html.ligue.body(ligue))
    }.result
  }

  /**
   * Show Ligue tournament
   * @param ligueShortName ligue
   * @param tournamentShortName tournament
   * @return the tournament page
   */
  def ligueTournament(ligueShortName: String, tournamentShortName: String) = Action {
    LigueAction(ligueShortName) {
      ligue => ligue.findTournamentByShortName(tournamentShortName) match {
        case Some(t) => Ok(views.html.tournament.ligue(t))
        case None => BadRequest(s"Tournoi non connu: $tournamentShortName dans la $ligue")
      }
    }.result
  }

  /**
   * Comite page
   * @param ligueShortName ligue short name
   * @param comiteShortName comite short name
   * @return the comite page
   */
  def comite(ligueShortName: String, comiteShortName: String) = Action {
    ComiteAction(ligueShortName, comiteShortName) {
      comite => Ok(views.html.comite.comite(comite))
    }.result
  }

  def comiteBody(ligueShortName: String, comiteShortName: String) = Action {
    ComiteAction(ligueShortName, comiteShortName) {
      comite => Ok(views.html.comite.body(comite))
    }.result
  }

  /**
   * Show comite tournament
   * @param ligueShortName ligue
   * @param tournamentShortName tournament
   * @return the tournament page
   */
  def comiteTournament(ligueShortName: String, comiteShortName: String, tournamentShortName: String) = Action {
    ComiteAction(ligueShortName, comiteShortName) {
      comite => comite.findTournamentByShortName(tournamentShortName) match {
        case Some(t) => Ok(views.html.tournament.comite(t))
        case None => BadRequest(s"Tournoi non connu: $tournamentShortName dans le $comite")
      }
    }.result
  }

  /**
   * Club page
   * @param ligueShortName ligue short name
   * @param comiteShortName comite short name
   * @param clubShortName club short name
   * @return the club page
   */
  def club(ligueShortName: String, comiteShortName: String, clubShortName: String) = Action {
    ComiteAction(ligueShortName, comiteShortName) {
      comite => comite.findClubByShortName(clubShortName) match {
        case Some(club) => Ok(views.html.club.club(club))
        case _ => BadRequest(s"Club non connue: $clubShortName dans le $comite")
      }
    }.result
  }

  /**
   * Show contacts
   * @return contacts page
   */
  def contact() = Action {
    Ok(views.html.contacts(Contact.contacts))
  }

  /**
   * Events
   * @return events page
   */
  def event() = Action {
    Ok(views.html.event.events())
  }

  /**
   * Events List
   * @return list page
   */
  def eventsList() = Action {
    Ok(views.html.event.list(Event.events))
  }

  /**
   * Events calendar
   * @return calendar page
   */
  def eventsCalendar() = Action {
    val years = EventYear.years(Event.events)
    Ok(views.html.event.calendar(years))
  }


  // a sample action using the new authorization hook
  def onlyTwitter = SecuredAction(WithProvider("twitter")) {
    implicit request =>
    //
    //    Note: If you had a User class and returned an instance of it from UserService, this
    //          is how you would convert Identity to your own class:
    //
    //    request.user match {
    //      case user: User => // do whatever you need with your user class
    //      case _ => // did not get a User instance, should not happen,log error/thow exception
    //    }
      Ok("You can see this because you logged in using Twitter")
  }

  // An Authorization implementation that only authorizes uses that logged in using twitter
  case class WithProvider(provider: String) extends Authorization {
    def isAuthorized(user: Identity) = {
      user.identityId.providerId == provider
    }
  }

}

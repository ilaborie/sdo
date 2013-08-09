package controllers

import play.api.mvc._
import model.orga._

import securesocial.core._

/**
 * Mains pages
 */
object Orga extends Controller with SecureSocial {

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
  def ligues = Action {
    Ok(views.html.ligues(Ligue.ligues))
  }

  /**
   * Ligue page
   * @param shortName the ligue short name
   * @return the ligue page
   */
  def ligue(shortName: String) = Action {
    implicit request =>
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
  def club(ligueShortName: String, comiteShortName: String, clubShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        val user = request.user
        comite => comite.findClubByShortName(clubShortName) match {
          case Some(club) => Ok(views.html.club.club(user, club))
          case _ => BadRequest(s"Club non connue: $clubShortName dans le $comite")
        }
      }.result
  }

}

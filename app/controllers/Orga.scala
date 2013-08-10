package controllers

import play.mvc.Controller
import securesocial.core.SecureSocial
import model.orga._
import model.user.User

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
  def ligues = SecuredAction {
    implicit request =>
      Ok(views.html.ligues(Ligue.ligues, User(request.user)))
  }

  /**
   * Ligue page
   * @param shortName the ligue short name
   * @return the ligue page
   */
  def ligue(shortName: String) = SecuredAction {
    implicit request =>
      LigueAction(shortName) {
        ligue => Ok(views.html.ligue.ligue(ligue, User(request.user)))
      }.result
  }

  /**
   * Ligue body page
   * @param shortName the ligue short name
   * @return the ligue page
   */
  def ligueBody(shortName: String) = SecuredAction {
    implicit request =>
      LigueAction(shortName) {
        ligue => Ok(views.html.ligue.body(ligue, User(request.user)))
      }.result
  }

  /**
   * Show Ligue tournament
   * @param ligueShortName ligue
   * @param tournamentShortName tournament
   * @return the tournament page
   */
  def ligueTournament(ligueShortName: String, tournamentShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => ligue.findTournamentByShortName(tournamentShortName) match {
          case Some(t) => Ok(views.html.tournament.ligue(t, User(request.user)))
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
  def comite(ligueShortName: String, comiteShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => Ok(views.html.comite.comite(comite, User(request.user)))
      }.result
  }

  def comiteBody(ligueShortName: String, comiteShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => Ok(views.html.comite.body(comite, User(request.user)))
      }.result
  }

  /**
   * Show comite tournament
   * @param ligueShortName ligue
   * @param tournamentShortName tournament
   * @return the tournament page
   */
  def comiteTournament(ligueShortName: String, comiteShortName: String, tournamentShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => comite.findTournamentByShortName(tournamentShortName) match {
          case Some(t) => Ok(views.html.tournament.comite(t, User(request.user)))
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
          case Some(club) => Ok(views.html.club.club(club, User(request.user)))
          case _ => BadRequest(s"Club non connue: $clubShortName dans le $comite")
        }
      }.result
  }

}

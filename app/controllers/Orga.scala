package controllers

import play.mvc.Controller
import play.api.mvc.Action

import model.orga._
import model.user.User

import securesocial.core.SecureSocial
import org.joda.time.LocalDate

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
    val today = LocalDate.now
    val events = model.event.Event.events.filter(_.to.isAfter(today)).take(3)
    val sdo = Ligue.findByShortName("SDO").get
    val mpy = Ligue.comites.find(_.shortName=="MPY").get
    val teg = Ligue.comites.find(_.shortName=="TEG").get
    val aqu = Ligue.comites.find(_.shortName=="AQU").get
    Ok(views.html.ligues(sdo, mpy, teg, aqu, events))
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

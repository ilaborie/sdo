package controllers

import play.api.mvc._
import model.orga._
import model.rank._
import securesocial.core._


/**
 * Classements pages
 */
object Classements extends Controller with SecureSocial {

  private val season: Season = Season.currentSeason

  /**
   * Ligue Single
   * @param ligueShortName ligue
   * @return single ranking
   */
  def ligueSingle(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => Ok(views.html.ligue.single(request.user, ligue, LigueRanking.single(ligue)))
      }.result
  }

  /**
   * Ligue Feminine
   * @param ligueShortName ligue
   * @return feminine ranking
   */
  def ligueFeminine(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => Ok(views.html.ligue.feminine(request.user, ligue, LigueRanking.feminine(ligue)))
      }.result
  }

  /**
   * Ligue Junior
   * @param ligueShortName ligue
   * @return junior ranking
   */
  def ligueJunior(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => Ok(views.html.ligue.junior(request.user, ligue, LigueRanking.junior(ligue)))
      }.result
  }

  /**
   * Ligue Double
   * @param ligueShortName ligue
   * @return double ranking
   */
  def ligueDoublette(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => Ok(views.html.ligue.double(request.user, ligue, LigueRanking.double(ligue)))
      }.result
  }

  /**
   * Ligue Team
   * @param ligueShortName ligue
   * @return team ranking
   */
  def ligueTeam(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => Ok(views.html.ligue.team(request.user, ligue, LigueRanking.team(ligue)))
      }.result
  }


  /**
   * Comite Single
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return single ranking
   */
  def comiteSingle(ligueShortName: String, comiteShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => Ok(views.html.comite.single(request.user, comite, ComiteRanking.single(comite)))
      }.result
  }

  /**
   * Comite Feminine
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return feminine ranking
   */
  def comiteFeminine(ligueShortName: String, comiteShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => Ok(views.html.comite.feminine(request.user, comite, ComiteRanking.feminine(comite)))
      }.result
  }

  /**
   * Comite Junior
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return junior ranking
   */
  def comiteJunior(ligueShortName: String, comiteShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => Ok(views.html.comite.junior(request.user, comite, ComiteRanking.junior(comite)))
      }.result
  }

  /**
   * Comite Doublette
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return doublette ranking
   */
  def comiteDoublette(ligueShortName: String, comiteShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => Ok(views.html.comite.double(request.user, comite, ComiteRanking.double(comite)))
      }.result
  }

  /**
   * Comite Team
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return team ranking
   */
  def comiteTeam(ligueShortName: String, comiteShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => Ok(views.html.comite.team(request.user, comite, ComiteRanking.team(comite)))
      }.result
  }
}

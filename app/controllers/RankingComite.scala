package controllers

import play.api.mvc._

import securesocial.core._

import util.pdf.PDF

import model.orga._
import model.rank._
import model.user.User


/**
 * Classements pages
 */
object RankingComite extends Controller with SecureSocial {

  /**
   * Comite Single
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return single ranking
   */
  def comiteSingle(ligueShortName: String, comiteShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => Ok(views.html.comite.single(comite, ComiteRanking.single(comite), User(request.user)))
      }.result
  }

  /**
   * Comite Single
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return single ranking
   */
  def comiteSinglePDF(ligueShortName: String, comiteShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => PDF.ok(pdf.html.comiteSingleRanking.render(comite, ComiteRanking.single(comite))).getWrappedResult
      }.result
  }

  /**
   * Comite Ladies
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return ladies ranking
   */
  def comiteLadies(ligueShortName: String, comiteShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => Ok(views.html.comite.ladies(comite, ComiteRanking.ladies(comite), User(request.user)))
      }.result
  }

  /**
   * Comite ladies
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return single ranking
   */
  def comiteLadiesPDF(ligueShortName: String, comiteShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => PDF.ok(pdf.html.comiteLadiesRanking.render(comite, ComiteRanking.ladies(comite))).getWrappedResult
      }.result
  }

  /**
   * Comite Youth
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return youth ranking
   */
  def comiteYouth(ligueShortName: String, comiteShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => Ok(views.html.comite.youth(comite, ComiteRanking.youth(comite), User(request.user)))
      }.result
  }

  /**
   * Comite youth
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return single ranking
   */
  def comiteYouthPDF(ligueShortName: String, comiteShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => PDF.ok(pdf.html.comiteYouthRanking.render(comite, ComiteRanking.youth(comite))).getWrappedResult
      }.result
  }

  /**
   * Comite Pairstte
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return doublette ranking
   */
  def comitePairs(ligueShortName: String, comiteShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => Ok(views.html.comite.pairs(comite, ComiteRanking.pairs(comite), User(request.user)))
      }.result
  }
  /**
   * Comite pairs
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return single ranking
   */
  def comitePairsPDF(ligueShortName: String, comiteShortName: String) = SecuredAction {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => PDF.ok(pdf.html.comitePairsRanking.render(comite, ComiteRanking.pairs(comite))).getWrappedResult
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
        comite => Ok(views.html.comite.team(comite, ComiteRanking.team(comite), User(request.user)))
      }.result
  }

  /**
   * Comite Team (PDF)
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return PDF
   */
  def comiteTeamPDF(ligueShortName: String, comiteShortName: String) = Action {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite =>
          PDF.ok(pdf.html.comiteTeamRanking.render(comite, ComiteRanking.team(comite))).getWrappedResult
      }.result
  }
}

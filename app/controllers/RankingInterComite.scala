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
object RankingInterComite extends Controller with SecureSocial {

  /**
   * InterComite Single
   * @param ligueShortName ligue
   * @return single ranking
   */
  def interComiteSingle(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => Ok(views.html.interComite.single(ligue, InterComiteRanking.single(ligue), User(request.user)))
      }.result
  }
  /**
   * Ligue Single  PDF
   * @param ligueShortName ligue
   * @return single ranking
   */
  def interComiteSinglePDF(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => PDF.ok(pdf.html.interComiteSingleRanking.render(ligue, InterComiteRanking.single(ligue))).getWrappedResult
      }.result
  }

  /**
   * Ligue Feminine
   * @param ligueShortName ligue
   * @return ladies ranking
   */
  def interComiteLadies(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => Ok(views.html.interComite.ladies(ligue, InterComiteRanking.ladies(ligue), User(request.user)))
      }.result
  }

  /**
   * Ligue Ladies
   * @param ligueShortName ligue
   * @return ladies ranking
   */
  def interComiteLadiesPDF(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => PDF.ok(pdf.html.interComiteLadiesRanking.render(ligue, InterComiteRanking.ladies(ligue))).getWrappedResult
      }.result
  }

  /**
   * Ligue Youth
   * @param ligueShortName ligue
   * @return youth ranking
   */
  def interComiteYouth(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => Ok(views.html.interComite.youth(ligue, InterComiteRanking.youth(ligue), User(request.user)))
      }.result
  }

  /**
   * Ligue Youth PDF
   * @param ligueShortName ligue
   * @return youth ranking
   */
  def interComiteYouthPDF(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => PDF.ok(pdf.html.interComiteYouthRanking.render(ligue, InterComiteRanking.youth(ligue))).getWrappedResult
      }.result
  }

  /**
   * Ligue Pairs
   * @param ligueShortName ligue
   * @return pairs ranking
   */
  def interComitePairs(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => Ok(views.html.interComite.pairs(ligue, InterComiteRanking.pairs(ligue), User(request.user)))
      }.result
  }

  /**
   * Ligue Pairs
   * @param ligueShortName ligue
   * @return pairs ranking
   */
  def interComitePairsPDF(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue =>
          PDF.ok(pdf.html.interComitePairsRanking.render(ligue, InterComiteRanking.pairs(ligue))).getWrappedResult
      }.result
  }

  /**
   * Ligue Team
   * @param ligueShortName ligue
   * @return team ranking
   */
  def interComiteTeam(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => Ok(views.html.interComite.team(ligue, InterComiteRanking.team(ligue), User(request.user)))
      }.result
  }

  /**
   * Team Ligue (PDF)
   * @param ligueShortName ligue
   * @return PDF
   */
  def interComiteTeamPDF(ligueShortName: String) = Action {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue =>
          PDF.ok(pdf.html.interComiteTeamRanking.render(ligue, InterComiteRanking.team(ligue))).getWrappedResult
      }.result
  }

}

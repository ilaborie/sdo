package controllers

import play.api.mvc._
import play.api.i18n.Messages

import securesocial.core._

import util.pdf.PDF

import model.orga._
import model.rank._
import model.user.User


/**
 * Classements pages
 */
object RankingLigue extends Controller with SecureSocial {

  /**
   * Ligue   PDF
   * @param ligueShortName ligue
   * @return single ranking
   */
  def liguePDF(ligueShortName: String) = Action {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => PDF.ok(pdf.html.ligueRanking.render(Season.currentSeason, ligue)).getWrappedResult
      }.result
  }

  /**
   * Ligue Single
   * @param ligueShortName ligue
   * @return single ranking
   */
  def ligueSingle(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => Ok(views.html.ligue.single(ligue, LigueRanking.single(ligue), User(request.user)))
      }.result
  }

  /**
   * Ligue Single  PDF
   * @param ligueShortName ligue
   * @return single ranking
   */
  def ligueSinglePDF(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue =>
          PDF.ok(pdf.html.rankingTable.render(
            LigueRanking.single(ligue),
            Messages("rank.single.ligue.caption", ligue.name, Season.currentSeason),
            LigueRanking.qualifyForMasterSingle
          )).getWrappedResult
      }.result
  }

  /**
   * Ligue Feminine
   * @param ligueShortName ligue
   * @return ladies ranking
   */
  def ligueLadies(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => Ok(views.html.ligue.ladies(ligue, LigueRanking.ladies(ligue), User(request.user)))
      }.result
  }

  /**
   * Ligue Ladies
   * @param ligueShortName ligue
   * @return ladies ranking
   */
  def ligueLadiesPDF(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue =>
          PDF.ok(pdf.html.rankingTable.render(
            LigueRanking.ladies(ligue),
            Messages("rank.feminine.ligue.caption", ligue.name, Season.currentSeason),
            LigueRanking.qualifyForMasterLadies
          )).getWrappedResult
      }.result
  }

  /**
   * Ligue Youth
   * @param ligueShortName ligue
   * @return youth ranking
   */
  def ligueYouth(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => Ok(views.html.ligue.youth(ligue, LigueRanking.youth(ligue), User(request.user)))
      }.result
  }

  /**
   * Ligue Youth PDF
   * @param ligueShortName ligue
   * @return youth ranking
   */
  def ligueYouthPDF(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue =>
          PDF.ok(pdf.html.rankingTable.render(
            LigueRanking.youth(ligue),
            Messages("rank.junior.ligue.caption", ligue.name, Season.currentSeason),
            LigueRanking.qualifyForMasterYouth
          )).getWrappedResult
      }.result
  }

  /**
   * Ligue Pairs
   * @param ligueShortName ligue
   * @return pairs ranking
   */
  def liguePairs(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => Ok(views.html.ligue.pairs(ligue, LigueRanking.pairs(ligue), User(request.user)))
      }.result
  }

  /**
   * Ligue Pairs
   * @param ligueShortName ligue
   * @return pairs ranking
   */
  def liguePairsPDF(ligueShortName: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue =>
          PDF.ok(pdf.html.rankingTable.render(
            LigueRanking.pairs(ligue),
            Messages("rank.double.ligue.caption", ligue.name, Season.currentSeason),
            LigueRanking.qualifyForMasterPairs
          )).getWrappedResult
      }.result
  }

}

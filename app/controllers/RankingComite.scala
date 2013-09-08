package controllers

import play.api.mvc._

import securesocial.core._

import util.pdf.PDF

import model.orga._
import model.rank._
import model.user.User
import play.api.i18n.Messages


/**
 * Classements pages
 */
object RankingComite extends Controller with SecureSocial {

  /**
   * Comite PDF
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return single ranking
   */
  def comitePDF(ligueShortName: String, comiteShortName: String) = Action {
    implicit request =>
      ComiteAction(ligueShortName, comiteShortName) {
        comite => PDF.ok(pdf.html.comiteRanking.render(Season.currentSeason, comite)).getWrappedResult
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
        comite =>
          PDF.ok(pdf.html.rankingTable.render(
            ComiteRanking.single(comite),
            Messages("rank.single.comite.caption", comite.name, Season.currentSeason),
            ComiteRanking.qualifyForMasterSingle
          )).getWrappedResult
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
        comite =>
          PDF.ok(pdf.html.rankingTable.render(
            ComiteRanking.ladies(comite),
            Messages("rank.feminine.comite.caption", comite.name, Season.currentSeason),
            ComiteRanking.qualifyForMasterLadies
          )).getWrappedResult
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
        comite =>
          PDF.ok(pdf.html.rankingTable.render(
            ComiteRanking.youth(comite),
            Messages("rank.youth.comite.caption", comite.name, Season.currentSeason),
            ComiteRanking.qualifyForMasterYouth
          )).getWrappedResult
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
        comite =>
          PDF.ok(pdf.html.rankingTable.render(
            ComiteRanking.pairs(comite),
            Messages("rank.double.comite.caption", comite.name, Season.currentSeason),
            ComiteRanking.qualifyForMasterPairs
          )).getWrappedResult
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

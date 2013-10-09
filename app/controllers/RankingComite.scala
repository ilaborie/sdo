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

package controllers

import play.api.mvc._


import util.pdf.PDF

import model.orga._
import model.rank._
import model.user.User
import play.api.i18n.Messages


/**
 * Classements pages
 */
object RankingComite extends Controller with ComiteController {

  /**
   * Comite PDF
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return single ranking
   */
  def comitePDF(ligueShortName: String, comiteShortName: String) = ComiteAsyncAction(ligueShortName, comiteShortName) {
    comite =>
      PDF.ok(pdf.html.comiteRanking.render(Season.currentSeason, comite))
  }

  /**
   * Comite Single
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return single ranking
   */
  def comiteSingle(ligueShortName: String, comiteShortName: String) = SecuredComiteAction(ligueShortName, comiteShortName, ajaxCall = true) {
    (comite, user) =>
      Ok(views.html.comite.single(comite, ComiteRanking.single(comite), User(user)))
  }

  /**
   * Comite Single
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return single ranking
   */
  def comiteSinglePDF(ligueShortName: String, comiteShortName: String) = SecuredComiteAsyncAction(ligueShortName, comiteShortName) {
    (comite, user) =>
      PDF.ok(pdf.html.rankingTable.render(
        ComiteRanking.single(comite),
        Messages("rank.single.comite.caption", comite.name, Season.currentSeason),
        ComiteRanking.qualifyForMasterSingle
      ))
  }

  /**
   * Comite Ladies
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return ladies ranking
   */
  def comiteLadies(ligueShortName: String, comiteShortName: String) = SecuredComiteAction(ligueShortName, comiteShortName, ajaxCall = true) {
    (comite, user) =>
      Ok(views.html.comite.ladies(comite, ComiteRanking.ladies(comite), User(user)))
  }

  /**
   * Comite ladies
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return single ranking
   */
  def comiteLadiesPDF(ligueShortName: String, comiteShortName: String) = SecuredComiteAsyncAction(ligueShortName, comiteShortName) {
    (comite, user) =>
      PDF.ok(pdf.html.rankingTable.render(
        ComiteRanking.ladies(comite),
        Messages("rank.feminine.comite.caption", comite.name, Season.currentSeason),
        ComiteRanking.qualifyForMasterLadies
      ))
  }

  /**
   * Comite Youth
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return youth ranking
   */
  def comiteYouth(ligueShortName: String, comiteShortName: String) = SecuredComiteAction(ligueShortName, comiteShortName, ajaxCall = true) {
    (comite, user) =>
      Ok(views.html.comite.youth(comite, ComiteRanking.youth(comite), User(user)))
  }

  /**
   * Comite youth
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return single ranking
   */
  def comiteYouthPDF(ligueShortName: String, comiteShortName: String) = SecuredComiteAsyncAction(ligueShortName, comiteShortName) {
    (comite, user) =>
      PDF.ok(pdf.html.rankingTable.render(
        ComiteRanking.youth(comite),
        Messages("rank.youth.comite.caption", comite.name, Season.currentSeason),
        ComiteRanking.qualifyForMasterYouth
      ))
  }

  /**
   * Comite Pairstte
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return doublette ranking
   */
  def comitePairs(ligueShortName: String, comiteShortName: String) = SecuredComiteAction(ligueShortName, comiteShortName, ajaxCall = true) {
    (comite, user) =>
      Ok(views.html.comite.pairs(comite, ComiteRanking.pairs(comite), User(user)))
  }

  /**
   * Comite pairs
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return single ranking
   */
  def comitePairsPDF(ligueShortName: String, comiteShortName: String) = SecuredComiteAsyncAction(ligueShortName, comiteShortName) {
    (comite, user) =>
      PDF.ok(pdf.html.rankingTable.render(
        ComiteRanking.pairs(comite),
        Messages("rank.double.comite.caption", comite.name, Season.currentSeason),
        ComiteRanking.qualifyForMasterPairs
      ))
  }

  /**
   * Comite Team
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return team ranking
   */
  def comiteTeam(ligueShortName: String, comiteShortName: String) = SecuredComiteAction(ligueShortName, comiteShortName, ajaxCall = true) {
    (comite, user) =>
      Ok(views.html.comite.team(comite, ComiteRanking.team(comite), User(user)))
  }

  /**
   * Comite Team (PDF)
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return PDF
   */
  def comiteTeamPDF(ligueShortName: String, comiteShortName: String) = ComiteAsyncAction(ligueShortName, comiteShortName) {
    comite =>
      PDF.ok(pdf.html.comiteTeamRanking.render(comite, ComiteRanking.team(comite)))
  }
}

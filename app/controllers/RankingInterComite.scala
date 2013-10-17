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
object RankingInterComite extends Controller with LigueController {

  /**
   * Inter comite PDF
   * @param ligueShortName ligue
   * @return single ranking
   */
  def interComitePDF(ligueShortName: String) = LigueAsyncAction(ligueShortName) {
    ligue =>
      PDF.ok(pdf.html.interComiteRanking.render(Season.currentSeason, ligue))
  }

  /**
   * InterComite Single
   * @param ligueShortName ligue
   * @return single ranking
   */
  def interComiteSingle(ligueShortName: String) = SecuredLigueAction(ligueShortName, ajaxCall = true) {
    (ligue, user) =>
      Ok(views.html.interComite.single(ligue, InterComiteRanking.mens(ligue), User(user)))
  }

  /**
   * Ligue Single  PDF
   * @param ligueShortName ligue
   * @return single ranking
   */
  def interComiteSinglePDF(ligueShortName: String) = SecuredLigueAsyncAction(ligueShortName) {
    (ligue, user) =>
      PDF.ok(pdf.html.rankingTable.render(
        InterComiteRanking.mens(ligue),
        Messages("rank.single.interComite.caption", Season.currentSeason)))
  }

  /**
   * Ligue Feminine
   * @param ligueShortName ligue
   * @return ladies ranking
   */
  def interComiteLadies(ligueShortName: String) = SecuredLigueAction(ligueShortName, ajaxCall = true) {
    (ligue, user) =>
      Ok(views.html.interComite.ladies(ligue, InterComiteRanking.ladies(ligue), User(user)))
  }

  /**
   * Ligue Ladies
   * @param ligueShortName ligue
   * @return ladies ranking
   */
  def interComiteLadiesPDF(ligueShortName: String) = SecuredLigueAsyncAction(ligueShortName) {
    (ligue, user) =>
      PDF.ok(pdf.html.rankingTable.render(
        InterComiteRanking.ladies(ligue),
        Messages("rank.feminine.interComite.caption", Season.currentSeason)))
  }

  /**
   * Ligue Youth
   * @param ligueShortName ligue
   * @return youth ranking
   */
  def interComiteYouth(ligueShortName: String) = SecuredLigueAction(ligueShortName, ajaxCall = true) {
    (ligue, user) =>
      Ok(views.html.interComite.youth(ligue, InterComiteRanking.youth(ligue), User(user)))
  }

  /**
   * Ligue Youth PDF
   * @param ligueShortName ligue
   * @return youth ranking
   */
  def interComiteYouthPDF(ligueShortName: String) = SecuredLigueAsyncAction(ligueShortName) {
    (ligue, user) =>
      PDF.ok(pdf.html.rankingTable.render(
        InterComiteRanking.youth(ligue),
        Messages("rank.junior.interComite.caption", Season.currentSeason)))
  }

  /**
   * Ligue Pairs
   * @param ligueShortName ligue
   * @return pairs ranking
   */
  def interComitePairs(ligueShortName: String) = SecuredLigueAction(ligueShortName, ajaxCall = true) {
    (ligue, user) =>
      Ok(views.html.interComite.pairs(ligue, InterComiteRanking.pairs(ligue), User(user)))
  }

  /**
   * Ligue Pairs
   * @param ligueShortName ligue
   * @return pairs ranking
   */
  def interComitePairsPDF(ligueShortName: String) = SecuredLigueAsyncAction(ligueShortName) {
    (ligue, user) =>
      PDF.ok(pdf.html.rankingTable.render(
        InterComiteRanking.pairs(ligue),
        Messages("rank.double.interComite.caption", Season.currentSeason)))
  }

  /**
   * Ligue Team
   * @param ligueShortName ligue
   * @return team ranking
   */
  def interComiteTeam(ligueShortName: String) = SecuredLigueAction(ligueShortName, ajaxCall = true) {
    (ligue, user) =>
      Ok(views.html.interComite.team(ligue, InterComiteRanking.team(ligue), User(user)))
  }

  /**
   * Team Ligue (PDF)
   * @param ligueShortName ligue
   * @return PDF
   */
  def interComiteTeamPDF(ligueShortName: String) = LigueAsyncAction(ligueShortName) {
    ligue =>
      PDF.ok(pdf.html.interComiteTeamRanking.render(ligue, InterComiteRanking.team(ligue)))
  }

}

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
import play.api.i18n.Messages


import util.pdf.PDF

import model.orga._
import model.rank._
import model.user.User


/**
 * Classements pages
 */
object RankingLigue extends Controller with LigueController {

  /**
   * Ligue   PDF
   * @param ligueShortName ligue
   * @return single ranking
   */
  def liguePDF(ligueShortName: String) = LigueAsyncAction(ligueShortName) {
    ligue => PDF.ok(pdf.html.ligueRanking.render(Season.currentSeason, ligue))
  }

  /**
   * Ligue Single
   * @param ligueShortName ligue
   * @return single ranking
   */
  def ligueMens(ligueShortName: String) = SecuredLigueAction(ligueShortName, ajaxCall = true) {
    (ligue, request) =>
      Ok(views.html.ligue.mens(ligue, LigueRanking.mens(ligue), User(request))(request))
  }

  /**
   * Ligue Single  PDF
   * @param ligueShortName ligue
   * @return single ranking
   */
  def ligueMensPDF(ligueShortName: String) = LigueAsyncAction(ligueShortName) {
    ligue =>
      PDF.ok(pdf.html.rankingTable.render(
        LigueRanking.mens(ligue),
        Messages("rank.single.ligue.caption", ligue.name, Season.currentSeason),
        Mens(ligue)))
  }

  /**
   * Ligue Feminine
   * @param ligueShortName ligue
   * @return ladies ranking
   */
  def ligueLadies(ligueShortName: String) = SecuredLigueAction(ligueShortName, ajaxCall = true) {
    (ligue, request) =>
      Ok(views.html.ligue.ladies(ligue, LigueRanking.ladies(ligue), User(request))(request))
  }

  /**
   * Ligue Ladies
   * @param ligueShortName ligue
   * @return ladies ranking
   */
  def ligueLadiesPDF(ligueShortName: String) = SecuredLigueAsyncAction(ligueShortName) {
    (ligue, user) =>
      PDF.ok(pdf.html.rankingTable.render(
        LigueRanking.ladies(ligue),
        Messages("rank.feminine.ligue.caption", ligue.name, Season.currentSeason),
        Ladies(ligue)))
  }

  /**
   * Ligue Youth
   * @param ligueShortName ligue
   * @return youth ranking
   */
  def ligueYouth(ligueShortName: String) = SecuredLigueAction(ligueShortName, ajaxCall = true) {
    (ligue, request) =>
      Ok(views.html.ligue.youth(ligue, LigueRanking.youth(ligue), User(request))(request))
  }

  /**
   * Ligue Youth PDF
   * @param ligueShortName ligue
   * @return youth ranking
   */
  def ligueYouthPDF(ligueShortName: String) = SecuredLigueAsyncAction(ligueShortName) {
    (ligue, user) =>
      PDF.ok(pdf.html.rankingTable.render(
        LigueRanking.youth(ligue),
        Messages("rank.junior.ligue.caption", ligue.name, Season.currentSeason),
        Youth(ligue)))
  }

  /**
   * Ligue Pairs
   * @param ligueShortName ligue
   * @return pairs ranking
   */
  def liguePairs(ligueShortName: String) = SecuredLigueAction(ligueShortName, ajaxCall = true) {
    (ligue, request) =>
      Ok(views.html.ligue.pairs(ligue, LigueRanking.pairs(ligue), User(request))(request))
  }

  /**
   * Ligue Pairs
   * @param ligueShortName ligue
   * @return pairs ranking
   */
  def liguePairsPDF(ligueShortName: String) = SecuredLigueAsyncAction(ligueShortName) {
    (ligue, user) =>
      PDF.ok(pdf.html.rankingTable.render(
        LigueRanking.pairs(ligue),
        Messages("rank.double.ligue.caption", ligue.name, Season.currentSeason),
        Pairs(ligue)))
  }

}

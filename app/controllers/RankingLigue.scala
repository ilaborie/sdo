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
  def ligueSingle(ligueShortName: String) = SecuredAction(ajaxCall = true) {
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
  def ligueLadies(ligueShortName: String) = SecuredAction(ajaxCall = true) {
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
  def ligueYouth(ligueShortName: String) = SecuredAction(ajaxCall = true) {
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
  def liguePairs(ligueShortName: String) = SecuredAction(ajaxCall = true) {
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

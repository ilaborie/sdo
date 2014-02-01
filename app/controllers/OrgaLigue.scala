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

import play.mvc.Controller
import play.api.mvc.Action

import model.orga._
import model.user.User

import org.joda.time.LocalDate
import util.pdf.PDF

/**
 * Mains pages
 */
object OrgaLigue extends Controller with LigueController {

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
    implicit request =>
      val today = LocalDate.now
      val events = model.event.Event.events.filter(_.to.isAfter(today)).take(3)
      val sdo = Ligue.findByShortName("SDO").get
      val mpy = Ligue.comites.find(_.shortName == "MPY").get
      val teg = Ligue.comites.find(_.shortName == "TEG").get
      Ok(views.html.ligues(sdo, mpy, teg, events))
  }

  /**
   * Ligue page
   * @param shortName the ligue short name
   * @return the ligue page
   */
  def ligue(shortName: String) = SecuredLigueAction(shortName) {
    (ligue, request) =>
      Ok(views.html.ligue.ligue(ligue, User(request))(request))
  }

  /**
   * Ligue body page
   * @param shortName the ligue short name
   * @return the ligue page
   */
  def ligueBody(shortName: String) = SecuredLigueAction(shortName, ajaxCall = true) {
    (ligue, request) =>
      Ok(views.html.ligue.body(ligue, User(request))(request))
  }

  /**
   * Show Ligue tournament
   * @param ligueShortName ligue
   * @param tournamentShortName tournament
   * @return the tournament page
   */
  def ligueTournament(ligueShortName: String, tournamentShortName: String) = SecuredLigueAction(ligueShortName, ajaxCall = true) {
    (ligue, user) =>
      ligue.findTournamentByShortName(tournamentShortName) match {
        case Some(t) =>
          t match {
            case bt: BaseTournament => Ok(views.html.tournament.ligue(bt, User(user)))
            case mt:MasterLigueTeam => Ok(views.html.tournament.ligueTeam(mt, User(user)))
          }
        case None => BadRequest(s"Tournoi non connu: $tournamentShortName dans la $ligue")
      }
  }

  /**
   * Show Ligue tournament
   * @param ligueShortName ligue
   * @param tournamentShortName tournament
   * @return the tournament page
   */
  //SecuredComiteAsyncAction
  def ligueTournamentPDF(ligueShortName: String, tournamentShortName: String) = LigueAsyncAction(ligueShortName) {
    ligue =>
      ligue.findTournamentByShortName(tournamentShortName) match {
        case Some(t) =>
          t match {
            case bt: BaseTournament => PDF.ok(pdf.html.ligueTournament(Season.currentSeason, bt))
          }
      }
  }
}

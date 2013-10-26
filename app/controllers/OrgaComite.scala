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

import model.user.User


/**
 * Mains pages
 */
object OrgaComite extends Controller with ComiteController {

  /**
   * Comite page
   * @param ligueShortName ligue short name
   * @param comiteShortName comite short name
   * @return the comite page
   */
  def comite(ligueShortName: String, comiteShortName: String) = SecuredComiteAction(ligueShortName, comiteShortName) {
    (comite, user) =>
      Ok(views.html.comite.comite(comite, User(user)))
  }

  def comiteBody(ligueShortName: String, comiteShortName: String) = SecuredComiteAction(ligueShortName, comiteShortName, ajaxCall = true) {
    (comite, request) =>
      Ok(views.html.comite.body(comite, User(request))(request))
  }

  /**
   * Show comite tournament
   * @param ligueShortName ligue
   * @param tournamentShortName tournament
   * @return the tournament page
   */
  def comiteTournament(ligueShortName: String, comiteShortName: String, tournamentShortName: String) = SecuredComiteAction(ligueShortName, comiteShortName, ajaxCall = true) {
    (comite, user) =>
      comite.findTournamentByShortName(tournamentShortName) match {
        case Some(t) => Ok(views.html.tournament.comite(t, User(user)))
        case None => BadRequest(s"Tournoi non connu: $tournamentShortName dans le $comite")
      }
  }

  /**
   * Club page
   * @param ligueShortName ligue short name
   * @param comiteShortName comite short name
   * @param clubShortName club short name
   * @return the club page
   */
  def club(ligueShortName: String, comiteShortName: String, clubShortName: String) = SecuredComiteAction(ligueShortName, comiteShortName, ajaxCall = true) {
    (comite, request) =>
      comite.findClubByShortName(clubShortName) match {
        case Some(club) => Ok(views.html.club.club(club, User(request))(request))
        case _ => BadRequest(s"Club non connue: $clubShortName dans le $comite")
      }
  }

}

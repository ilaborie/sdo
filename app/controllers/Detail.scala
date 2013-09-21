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

import securesocial.core._

import model.user.User
import model.orga._
import model.team._
import util.Mailer
import play.api.libs.json.JsString
import util.pdf.PDF

/**
 * Detail pages
 */
object Detail extends Controller with SecureSocial {

  private val season: Season = Season.currentSeason

  /**
   * Team match detail
   * @param ligueShortName ligue
   * @param day the day
   * @param team1Name first team name
   * @param team2Name second team name
   * @return Team match detail
   */
  def team(ligueShortName: String, day: Int, team1Name: String, team2Name: String) = SecuredAction {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => TeamChampionship(season, ligue).findDay(day) match {
          case None => BadRequest(s"Journée $day non trouvée !")
          case Some(champDay) => {
            val user = User(request.user)
            val team1 = ligue.findTeamByShortName(team1Name)
            val team2 = ligue.findTeamByShortName(team2Name)

            if (team1.isDefined && team2.isDefined) {
              val m: Option[PlannedTeamMatch] = champDay.findMatch(team1.get, team2.get)
              if (m.isDefined) {
                val detail: Option[MatchDetail] = m.get.detail
                if (detail.isDefined) Ok(views.html.team.teamDetail(ligue, detail.get, user))
                else Ok(views.html.team.teamDetailPlay(ligue, m.get, user))
              }
              else BadRequest(s"Match $team1Name - $team2Name non trouvée dans la journée $day !")
            } else if (team1.isDefined) BadRequest(s"Équipe $team2Name non trouvée !")
            else BadRequest(s"Équipe $team1Name non trouvée !")
          }
        }
      }.result
  }

  /**
   * Team match detail (PDF)
   * @param ligueShortName ligue
   * @param day day
   * @param team1Name team1
   * @param team2Name team3
   * @return
   */
  def teamPDF(ligueShortName: String, day: Int, team1Name: String, team2Name: String) = Action {
    LigueAction(ligueShortName) {
      ligue => TeamChampionship(season, ligue).findDay(day) match {
        case None => BadRequest(s"Journée $day non trouvée !")
        case Some(champDay) => {
          val team1 = ligue.findTeamByShortName(team1Name)
          val team2 = ligue.findTeamByShortName(team2Name)

          if (team1.isDefined && team2.isDefined) {
            val m: Option[PlannedTeamMatch] = champDay.findMatch(team1.get, team2.get)
            if (m.isDefined) {
              val detail: Option[MatchDetail] = m.get.detail
              if (detail.isDefined)
                PDF.ok(pdf.html.teamDetail(ligue, detail.get)).getWrappedResult
              else BadRequest(s"Match $team1Name - $team2Name non joué pour la journée $day !")
            }
            else BadRequest(s"Match $team1Name - $team2Name non trouvée dans la journée $day !")
          } else if (team1.isDefined) BadRequest(s"Équipe $team2Name non trouvée !")
          else BadRequest(s"Équipe $team1Name non trouvée !")
        }
      }
    }.result
  }

  /**
   * Handle team result
   * @param ligueShortName ligue
   * @return Ok or BadRequest
   */
  def result(ligueShortName: String) = Action {
    request =>
      request.body.asJson.map {
        json =>
        // Send a mail
          val result = json \ "result"

          val t1 = result \ "team1"
          val team1Name = (t1 \ "name").as[JsString].value
          val team1 = Ligue.teams.find(_.shortName == team1Name).get

          val t2 = result \ "team2"
          val team2Name = (t2 \ "name").as[JsString].value
          val team2 = Ligue.teams.find(_.shortName == team2Name).get

          val body = emails.html.teamResult(json, result, team1, t1, team2, t2)
          Mailer.sendEmail("[SDO] New Result", "ilaborie@gmail.com", body)
          // FIXME return a PDF...
          Ok( """{ok:true}""")
      }.getOrElse {
        BadRequest("Expecting Json data")
      }
  }
}

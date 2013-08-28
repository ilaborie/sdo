package controllers


import play.api.mvc._

import securesocial.core._

import model.user.User
import model.orga._
import model.team._
import util.Mailer
import play.api.libs.json.JsString

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
          val team1 = Ligue.teams.find(_.name == team1Name).get

          val t2 = result \ "team2"
          val team2Name = (t2 \ "name").as[JsString].value
          val team2 = Ligue.teams.find(_.name == team2Name).get

          val body = emails.html.teamResult(json, result, team1, t1, team2, t2)
          Mailer.sendEmail("[SDO] New Result", "ilaborie@gmail.com", body)
          Ok( """{ok:true}""")
      }.getOrElse {
        BadRequest("Expecting Json data")
      }
  }
}

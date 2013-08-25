package controllers


import play.api.mvc._
import play.api.libs.json._
import play.api.Play.current
import play.libs.Akka

import securesocial.core._
import securesocial.core.IdentityId


import model.orga._
import model.team._
import model.user.User
import model.team.PlannedTeamMatch


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
          import com.typesafe.plugin._
          import scala.concurrent.duration._
          import play.api.libs.concurrent.Execution.Implicits._

          // Send a mail
          Akka.system.scheduler.scheduleOnce(1 seconds) {
            val mail = use[MailerPlugin].email
            mail.setSubject("[SDO] New Result")
            mail.addRecipient("ilaborie@gmail.com")
            mail.addFrom("ilaborie@gmail.com")

            val result = json \ "result"

            val t1 = result \ "team1"
            val team1Name = (t1 \ "name").as[JsString].value
            val team1 = Ligue.teams.find(_.name == team1Name).get

            val t2 = result \ "team2"
            val team2Name = (t2 \ "name").as[JsString].value
            val team2 = Ligue.teams.find(_.name == team2Name).get

            val matches = (result \ "matches").as[JsArray].value.zipWithIndex.map {
              case (js, index) => matchAsString(js, index)
            }

            val body = s"""
day: d${json \ "day"}
file: ${team1.shortName}-${team2.shortName}.yml
comment:
${json \ "comment"}

===
date: ${result \ "date"}
location: ${result \ "location"}
team1: ${teamAsString(team1, t1)}
team2: ${teamAsString(team2, t2)}
matches: ${matches.mkString("  ")}
===
"""
            mail.send(body, "")
          }
          Ok("""{ok:true}""")
      }.getOrElse {
        BadRequest("Expecting Json data")
      }
  }

  /**
   * Match as String
   * @param json JSON data
   * @param index index
   * @return the string
   */
  private def matchAsString(json: JsValue, index: Int) = {
    s"""
  match${index + 1}:
    l1: ${legAsString(json \ "leg1")}
    l2: ${legAsString(json \ "leg2")}
    l3: ${legAsString(json \ "leg3")}"""
  }

  /**
   * Leg as String
   * @param json JSON data
   * @return the string
   */
  private def legAsString(json: JsValue) = {
    json.as[JsNumber].value.toInt match {
      case 1 => "1"
      case 2 => "2"
      case _ => ""
    }
  }

  /**
   * Team as String
   * @param team team
   * @param json JSON data
   * @return the string
   */
  private def teamAsString(team: Team, json: JsValue) = {
    val substitute = json \ "substitute"
    s"""
  team: ${team.shortName}
  capitain: ${json \ "capitain"}
  joueurs:
    - ${(json \ "players").as[JsArray].value.map(_.as[String]).mkString("\n    - ")}
  doubles:
    -
      j1: ${ json \ "d1" \ "j1" }
      j2: ${ json \ "d1" \ "j2" }
    -
      j1: ${ json \ "d2" \ "j1" }
      j2: ${ json \ "d2" \ "j2" }
  substitute:
    j: ${nullableAsString(substitute \ "j")}
    out: ${nullableAsString(substitute \ "out")}
    match: ${nullableAsString(substitute \ "out")}"""
  }

  /**
   * Nullable as string
   * @param json json
   * @return string
   */
  private def nullableAsString(json: JsValue) = {
    val s = json.toString()
    if (s != "null") s else ""
  }

}

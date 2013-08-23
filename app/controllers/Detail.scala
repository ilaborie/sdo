package controllers

import play.api.mvc._
import model.orga._
import model.team._
import securesocial.core._
import model.user.User


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
  /* FIXME DEV
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
  */
  def team(ligueShortName: String, day: Int, team1Name: String, team2Name: String) = Action {
    implicit request =>
      LigueAction(ligueShortName) {
        ligue => TeamChampionship(season, ligue).findDay(day) match {
          case None => BadRequest(s"Journée $day non trouvée !")
          case Some(champDay) => {
            val identityId = IdentityId("ilaborie@gmail.com", "userpass")
            val user = User(UserService.find(identityId).get)
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

}

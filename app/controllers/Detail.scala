package controllers

import play.api.mvc._
import model.orga._
import model.team.{MatchDetail, PlannedTeamMatch, TeamChampionship}


/**
 * Detail pages
 */
object Detail extends Controller {

  private val season: Season = Data.currentSeason

  /**
   * Team match detail
   * @param ligueShortName ligue
   * @param day the day
   * @param team1Name first team name
   * @param team2Name second team name
   * @return Team match detail
   */
  def team(ligueShortName: String, day: Int, team1Name: String, team2Name: String) = Action {
    LigueAction(ligueShortName) {
      ligue => TeamChampionship(season).findDay(day) match {
        case None => BadRequest(s"Journée $day non trouvée !")
        case Some(champDay) => {
          val team1 = ligue.findTeamByShortName(team1Name)
          val team2 = ligue.findTeamByShortName(team2Name)

          if (team1.isDefined && team2.isDefined) {
            val m: Option[PlannedTeamMatch] = champDay.findMatch(team1.get, team2.get)
            if (m.isDefined) {
              val detail: Option[MatchDetail] = m.get.detail
              if (detail.isDefined) Ok(views.html.classement.teamDetail(ligue, detail.get))
              else Ok(views.html.classement.teamDetailPlay(ligue, m.get))
            }
            else BadRequest(s"Match $team1Name - $team2Name non trouvée dans la journée $day !")
          } else if (team1.isDefined) BadRequest(s"Équipe $team2Name non trouvée !")
          else BadRequest(s"Équipe $team1Name non trouvée !")
        }
      }
    }.result
  }

}

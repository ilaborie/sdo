package model.tournament

import java.util.Calendar
import model.orga.Team


/**
 * User: igorlaborie
 * Date: 23/07/13
 * Time: 08:41
 */
sealed abstract class TeamMatch {

}

case class PlannedTeamMatch(team1: Team, team2: Team, date: Calendar) extends TeamMatch

case class FinishedTeamMatch(team1: Team, team2: Team, date: Calendar, score1: TeamScore, score2: TeamScore) extends TeamMatch


case class TeamScore(team: Team, matchWin: Int, legs: Int)

// FIXME Detail

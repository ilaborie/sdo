package model.team

import model.orga.Season

/**
 * Team Championship
 */
case class Championship(season: Season, days: List[ChampionshipDay])

/**
 * A Championship day
 * @param day day
 * @param matchs matchs
 */
case class ChampionshipDay(day: Int, matchs: Seq[PlannedTeamMatch])


package model.team

import model.orga.{Season, Ligue}

/**
 * Team TeamChampionship
 */
case class TeamChampionship(season: Season, days: List[TeamChampionshipDay])

object TeamChampionship {

  def apply(season:Season) : TeamChampionship = DataChampionship.readChampionship(season,Ligue.ligues.head)
}

/**
 * A TeamChampionship day
 * @param day day
 * @param matches matchs
 */
case class TeamChampionshipDay(day: Int, matches: Seq[PlannedTeamMatch])


package model.team

import model.orga.{Season, Ligue}

/**
 * Team Championship
 */
case class Championship(season: Season, days: List[ChampionshipDay])

object Championship {
  lazy val championship = DataChampionship.readChampionship(Ligue.ligues.head)
}

/**
 * A Championship day
 * @param day day
 * @param matches matchs
 */
case class ChampionshipDay(day: Int, matches: Seq[PlannedTeamMatch])


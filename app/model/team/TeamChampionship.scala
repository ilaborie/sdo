package model.team

import model.orga.{Comite, Season, Ligue}
import java.util.Calendar

/**
 * Team TeamChampionship
 */
case class TeamChampionship(season: Season, days: List[TeamChampionshipDay])

object TeamChampionship {

  def apply(season: Season): TeamChampionship = DataChampionship.readChampionship(season, Ligue.ligues.head)

  def apply(season: Season, comite: Comite): TeamChampionship = {
    val days = for {
      ds <- TeamChampionship(season).days
    } yield TeamChampionshipDay(comite.ligue, ds.day, ds.from, ds.to, ds.matches.filter(_.applyTo(comite)))

    TeamChampionship(season, days)
  }
}

/**
 * A TeamChampionship day
 * @param day day
 * @param matches matchs
 */
case class TeamChampionshipDay(ligue: Ligue, day: Int, from: Calendar, to: Calendar, matches: Seq[PlannedTeamMatch])

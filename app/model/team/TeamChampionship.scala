package model.team

import model.orga.{Team, Comite, Season, Ligue}
import java.util.Calendar
import model.event.Event

/**
 * Team TeamChampionship
 */
case class TeamChampionship(season: Season, ligue: Ligue, days: List[TeamChampionshipDay]) {
  override val toString = s"TeamChampionship $season - $ligue"

  def findDay(day: Int): Option[TeamChampionshipDay] = days.find(_.day == day)

  lazy val events = for (day <- days) yield Event(ligue, day)
}

object TeamChampionship {

  // FIXME Cache
  def apply(season: Season, ligue: Ligue): TeamChampionship = DataChampionship.readChampionship(season, ligue)

  def apply(season: Season, comite: Comite): TeamChampionship = {
    val days = for {
      ds <- TeamChampionship(season, comite.ligue).days
    } yield TeamChampionshipDay(comite.ligue, ds.day, ds.from, ds.to, ds.matches.filter(_.applyTo(comite)))

    TeamChampionship(season, comite.ligue, days)
  }
}

/**
 * A TeamChampionship day
 * @param day day
 * @param matches matchs
 */
case class TeamChampionshipDay(ligue: Ligue, day: Int, from: Calendar, to: Calendar, matches: Seq[PlannedTeamMatch]) {
  def findMatch(team1: Team, team2: Team): Option[PlannedTeamMatch] =
    matches.find(m => m.applyTo(team1) && m.applyTo(team2))

}

package model.team

import model.orga.{Team, Comite, Season, Ligue}
import model.event.Event
import org.joda.time.LocalDate

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
case class TeamChampionshipDay(ligue: Ligue, day: Int, from: LocalDate, to: LocalDate, matches: Seq[PlannedTeamMatch]) {

  def findMatch(team1: Team, team2: Team): Option[PlannedTeamMatch] =
    matches.find(m => m.applyTo(team1) && m.applyTo(team2))

  lazy val teamExempted: Seq[Team] = {
    val teams = for {
      ptm <- matches
      team <- ptm.teamsAsList
    } yield team

    ligue.teams.filter(team => !team.omit && !teams.contains(team))
  }

  def canBeingPlay(date:LocalDate):Boolean = day == 1
    // FIXME (date.isEqual(from) ||date.isAfter(from)) && (date.isEqual(to) ||date.isBefore(to))
}

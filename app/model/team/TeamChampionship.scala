// The MIT License (MIT)
//
// Copyright (c) 2013 Igor Laborie
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of
// this software and associated documentation files (the "Software"), to deal in
// the Software without restriction, including without limitation the rights to
// use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
// the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
// IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
// CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package model.team

import org.joda.time.LocalDate

import play.api.cache.Cache
import play.api.Play.current

import model.orga.{Team, Comite, Season, Ligue}
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

  def apply(season: Season, ligue: Ligue): TeamChampionship = Cache.getOrElse[TeamChampionship](s"$season|$ligue") {
    DataChampionship.readChampionship(season, ligue)
  }

  def apply(season: Season, comite: Comite): TeamChampionship = Cache.getOrElse[TeamChampionship](s"$season|$comite") {
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

  def canBeingPlay(date: LocalDate): Boolean =
    (date.isEqual(from) || date.isAfter(from)) && (date.isEqual(to) || date.isBefore(to))
}

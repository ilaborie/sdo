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

import model.orga._
import org.joda.time.LocalDate


/**
 * Team Match Detail
 */
case class TeamMatchDetail(team: Team,
                           capitain: LicensedPlayer,
                           players: Array[LicensedPlayer],
                           substitute: Option[Substitute],
                           doublettes: (TeamPair, TeamPair)) {
  require(players.size == 4)
  // Check Club
  require(players(0).club == team.club)
  require(players(1).club == team.club)
  require(players(2).club == team.club)
  require(players(3).club == team.club)
  require(substitute.isEmpty || (substitute.get.player.club == team.club))
  require(substitute.isEmpty || substitute.get.replace.isEmpty || (substitute.get.replace.get.club == team.club))
  // Check inputFieldConstructor playersList
  require(players.contains(doublettes._1.player1))
  require(players.contains(doublettes._1.player2))
  require(players.contains(doublettes._2.player1))
  require(players.contains(doublettes._2.player2))
  require(substitute.isEmpty || substitute.get.replace.isEmpty || players.contains(substitute.get.replace.get))

  def doSubstitution(participant: TeamParticipant): TeamParticipant = {
    val replace = substitute.get.replace.get
    val replaceBy = substitute.get.player
    participant match {
      case lp:LicensedPlayer=>
        if (participant == replace) replaceBy else participant
      case TeamPair(p1, p2) =>
        if (p1 == replace) TeamPair(replaceBy, p2)
        else if (p2 == replace) TeamPair(p1, replaceBy)
        else participant
      case _ => participant
    }
  }

  def getPlayer1(index: Int): TeamParticipant = {
    val basic = TeamMatchDetail.getPlayer1Index(index) match {
      case "p1" => players(0)
      case "p2" => players(1)
      case "p3" => players(2)
      case "p4" => players(3)
      case "d1" => doublettes._1
      case "d2" => doublettes._2
    }

    if (substitute.isDefined && substitute.get.replace.isDefined && index >= substitute.get.afterMatch.get) {
      doSubstitution(basic)
    } else basic
  }

  def getPlayer2(index: Int): TeamParticipant = {
    val basic = TeamMatchDetail.getPlayer2Index(index) match {
      case "p1" => players(0)
      case "p2" => players(1)
      case "p3" => players(2)
      case "p4" => players(3)
      case "d1" => doublettes._1
      case "d2" => doublettes._2
    }

    if (substitute.isDefined && substitute.get.replace.isDefined && index >= substitute.get.afterMatch.get) {
      doSubstitution(basic)
    } else basic
  }
}

object TeamMatchDetail {
  def isPlayer1Start(index: Int): Boolean = Array(1, 3, 5, 6, 8, 10, 11, 12, 18, 19).contains(index)


  def getPlayer1Index(index: Int): String = index match {
    case 1 => "p1"
    case 7 => "p1"
    case 11 => "p1"
    case 17 => "p1"

    case 2 => "p2"
    case 8 => "p2"
    case 13 => "p2"
    case 19 => "p2"

    case 3 => "p3"
    case 9 => "p3"
    case 12 => "p3"
    case 20 => "p3"

    case 4 => "p4"
    case 10 => "p4"
    case 14 => "p4"
    case 18 => "p4"

    case 5 => "d1"
    case 15 => "d1"

    case 6 => "d2"
    case 16 => "d2"
  }

  def getPlayer2Index(index: Int): String = index match {
    case 2 => "p1"
    case 7 => "p1"
    case 12 => "p1"
    case 18 => "p1"

    case 1 => "p2"
    case 8 => "p2"
    case 14 => "p2"
    case 20 => "p2"

    case 4 => "p3"
    case 9 => "p3"
    case 11 => "p3"
    case 19 => "p3"

    case 3 => "p4"
    case 10 => "p4"
    case 13 => "p4"
    case 17 => "p4"

    case 5 => "d1"
    case 16 => "d1"

    case 6 => "d2"
    case 15 => "d2"
  }
}


/**
 * Substitute
 * @param player player
 * @param replace replaced player
 * @param afterMatch match
 */
case class Substitute(player: LicensedPlayer, replace: Option[LicensedPlayer], afterMatch: Option[Int]) {
  require((replace.isDefined && afterMatch.isDefined) || (replace.isEmpty && afterMatch.isEmpty))
  require(afterMatch.isEmpty || (1 to 20).contains(afterMatch.get))
}

/**
 * A Single match
 * @param player1 player1
 * @param player2  player2
 * @param player1Start is the player1 start
 * @param legs legs
 */
case class Match(team1: Team,
                 team2: Team,
                 player1: TeamParticipant,
                 player2: TeamParticipant,
                 player1Start: Boolean,
                 legs: (Leg, Leg, Option[Leg])) {
  require((legs._1.winner == player1) || (legs._1.winner == player2))
  require((legs._2.winner == player1) || (legs._2.winner == player2))
  require(legs._3.isEmpty || (legs._3.get.winner == player1) || (legs._3.get.winner == player2))
  require((legs._3.isEmpty && (legs._1.winner == legs._2.winner)) ||
    (legs._3.isDefined && (legs._1.winner != legs._2.winner)))

  val winner: TeamParticipant = if (legs._1.winner == legs._2.winner) legs._1.winner else legs._3.get.winner

  val teamWinner: Team = if (winner == player1) team1 else team2

  val legsAsList: List[Leg] = if (legs._3.isDefined) List(legs._1, legs._2, legs._3.get) else List(legs._1, legs._2)
}

/**
 * Leg
 * @param winner winner
 */
case class Leg(winner: TeamParticipant)

/**
 * Team Score
 */
case class TeamScore(team: Team, matchWin: Int, legs: Int)

/**
 * Planned match
 */
case class PlannedTeamMatch(day: Int, team1: Team, team2: Team, detail: Option[MatchDetail]) {

  override val toString = s"[J$day] ${team1.shortName} - ${team2.shortName}"

  val teamsAsList: List[Team] = List(team1, team2)

  def applyTo(team: Team): Boolean = (team1 == team || team2 == team) && !team.omit

  def applyTo(comite: Comite): Boolean = team1.comite == comite && team2.comite == comite

  def applyTo(comite: Comite, team: Team): Boolean = applyTo(comite) && applyTo(team)

}

/**
 * Match Detail
 */
sealed abstract class MatchDetail {

  override def toString = s"[J$day] ${team1.shortName} - ${team2.shortName}"

  def day: Int

  def team1: Team

  def team2: Team

  def winner: Option[Team]

  def win(team: Team): Boolean

  def loose(team: Team): Boolean

  def draw(team: Team): Boolean

  def fail(team: Team): Boolean

  def plus(team: Team): Int

  def minus(team: Team): Int

  def legs(team: Team): Int

  def points(team: Team): Int = {
    if (win(team)) 3
    else if (draw(team)) 2
    else if (fail(team)) 0
    else 1
  }
}

case class MatchDetailFail(day: Int, team1: Team, team2: Team, fails: Seq[Team]) extends MatchDetail {
  require(!fails.isEmpty)
  require(fails.filter(t => t == team1 || t == team2).isEmpty)

  def win(team: Team): Boolean = {
    require(team1 == team || team2 == team)
    !fails.contains(team)
  }

  def loose(team: Team): Boolean = {
    require(team1 == team || team2 == team)
    false
  }

  def draw(team: Team): Boolean = {
    require(team1 == team || team2 == team)
    false
  }

  def fail(team: Team): Boolean = {
    require(team1 == team || team2 == team)
    fails.contains(team)
  }

  val winner: Option[Team] = {
    if (fails.contains(team1) && fails.contains(team2)) None
    else if (fails.contains(team1)) Some(team2)
    else Some(team1)
  }

  def plus(team: Team): Int = {
    require(team1 == team || team2 == team)
    if (win(team)) 20 else 0
  }

  def minus(team: Team): Int = {
    require(team1 == team || team2 == team)
    if (loose(team)) 20 else 0
  }

  def legs(team: Team): Int = {
    require(team1 == team || team2 == team)
    if (win(team)) 40 else 0
  }

}

/**
 * Match Detail
 * @param day day
 * @param date date
 * @param location location
 * @param team1Detail team1
 * @param team2Detail team2
 * @param matches matches
 */
case class PlayedMatchDetail(day: Int,
                             date: LocalDate,
                             location: String,
                             team1Detail: TeamMatchDetail,
                             team2Detail: TeamMatchDetail,
                             matches: List[Match]) extends MatchDetail {
  require(matches.size == 20)

  val team1 = team1Detail.team
  val team2 = team2Detail.team


  val score1: TeamScore = {
    val legs = {
      for {
        m <- matches
        leg <- m.legsAsList
        if leg.winner == m.player1
      } yield leg
    }.size

    TeamScore(team1, matches.count(m => m.winner == m.player1), legs)
  }
  val score2: TeamScore = {
    val legs = {
      for {
        m <- matches
        leg <- m.legsAsList
        if leg.winner == m.player2
      } yield leg
    }.size

    TeamScore(team2, matches.count(m => m.winner == m.player2), legs)
  }

  val winner: Option[Team] = {
    if (score1.matchWin > score2.matchWin) Some(team1)
    else if (score1.matchWin < score2.matchWin) Some(team2)
    else None
  }

  def win(team: Team): Boolean = {
    require(team1 == team || team2 == team)
    winner.isDefined && winner.get == team
  }

  def loose(team: Team): Boolean = {
    require(team1 == team || team2 == team)
    winner.isDefined && winner.get != team
  }

  def draw(team: Team): Boolean = {
    require(team1 == team || team2 == team)
    winner.isEmpty
  }

  def fail(team: Team): Boolean = {
    require(team1 == team || team2 == team)
    false
  }


  def plus(team: Team): Int = {
    require(team1 == team || team2 == team)
    matches.count(_.teamWinner == team)
  }

  def minus(team: Team): Int = {
    require(team1 == team || team2 == team)
    matches.count(_.teamWinner != team)
  }

  def legs(team: Team): Int = {
    require(team1 == team || team2 == team)

    //  2* win + loose this 3 legs
    2 * plus(team) + matches.count(m => (m.teamWinner != team) && (m.legsAsList.size == 3))
  }
}


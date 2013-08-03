package model.team

import model.orga._
import java.util.Calendar


/**
 * Team Match Detail
 */
case class TeamMatchDetail(team: Team,
                           capitain: LicensedPlayer,
                           players: Array[LicensedPlayer],
                           substitute: Option[Substitute],
                           doublettes: (TeamDoublette, TeamDoublette)) {
  require(players.size == 4)
  // Check Club
  require(players(0).club == team.club)
  require(players(1).club == team.club)
  require(players(2).club == team.club)
  require(players(3).club == team.club)
  require(substitute.isEmpty || (substitute.get.player.club == team.club))
  require(substitute.isEmpty || substitute.get.replace.isEmpty || (substitute.get.replace.get.club == team.club))
  // Check in playersList
  require(players.contains(doublettes._1.player1))
  require(players.contains(doublettes._1.player2))
  require(players.contains(doublettes._2.player1))
  require(players.contains(doublettes._2.player2))
  require(substitute.isEmpty || substitute.get.replace.isEmpty || players.contains(substitute.get.replace.get))

  def doSubstitution(participant: TeamParticipant): TeamParticipant = {
    val replace = substitute.get.replace.get
    val replaceBy = substitute.get.player
    participant match {
      case LicensedPlayer(_, _, _, _, _) =>
        if (participant == replace) replaceBy else participant
      case TeamDoublette(p1, p2) =>
        if (p1 == replace) TeamDoublette(replaceBy, p2)
        else if (p2 == replace) TeamDoublette(p1, replaceBy)
        else participant
      case _ => participant
    }
  }

  def isPlayer1Start(index: Int): Boolean = Array(1, 3, 5, 6, 8, 10, 11, 12, 18, 19).contains(index)

  def getPlayer1(index: Int): TeamParticipant = {
    val basic = index match {
      case 1 => players(0)
      case 7 => players(0)
      case 11 => players(0)
      case 17 => players(0)

      case 2 => players(1)
      case 8 => players(1)
      case 13 => players(1)
      case 19 => players(1)

      case 3 => players(2)
      case 9 => players(2)
      case 12 => players(2)
      case 20 => players(2)

      case 4 => players(3)
      case 10 => players(3)
      case 14 => players(3)
      case 18 => players(3)

      case 5 => doublettes._1
      case 15 => doublettes._1

      case 6 => doublettes._2
      case 16 => doublettes._2
    }

    if (substitute.isDefined && substitute.get.replace.isDefined && index >= substitute.get.afterMatch.get) {
      doSubstitution(basic)
    } else basic
  }

  def getPlayer2(index: Int): TeamParticipant = {
    val basic = index match {
      case 2 => players(0)
      case 7 => players(0)
      case 12 => players(0)
      case 18 => players(0)

      case 1 => players(1)
      case 8 => players(1)
      case 14 => players(1)
      case 20 => players(1)

      case 4 => players(2)
      case 9 => players(2)
      case 11 => players(2)
      case 19 => players(2)

      case 3 => players(3)
      case 10 => players(3)
      case 13 => players(3)
      case 17 => players(3)

      case 5 => doublettes._1
      case 16 => doublettes._1

      case 6 => doublettes._2
      case 15 => doublettes._2
    }

    if (substitute.isDefined && substitute.get.replace.isDefined && index >= substitute.get.afterMatch.get) {
      doSubstitution(basic)
    } else basic
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

  override val toString = s"[J$day] $team1 - $team2"

  def applyTo(team: Team): Boolean = (team1 == team || team2 == team) && !team.omit

  def applyTo(comite: Comite): Boolean = team1.comite == comite && team2.comite == comite

  def applyTo(comite: Comite, team: Team): Boolean = applyTo(comite) && applyTo(team)
}

/**
 * Match Detail
 */
sealed abstract class MatchDetail {

  override def toString = s"[J$day] ${team1.shortname} - ${team2.shortname}"

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
                             date: Calendar,
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
    require(team1 == team || team2== team)
    winner.isDefined && winner.get == team
  }

  def loose(team: Team): Boolean = {
    require(team1 == team || team2== team)
    winner.isDefined && winner.get != team
  }

  def draw(team: Team): Boolean = {
    require(team1 == team || team2== team)
    winner.isEmpty
  }

  def fail(team: Team): Boolean = {
    require(team1 == team || team2== team)
    false
  }


  def plus(team: Team): Int = {
    require(team1 == team || team2== team)
    matches.count(_.teamWinner == team)
  }

  def minus(team: Team): Int = {
    require(team1 == team || team2== team)
    matches.count(_.teamWinner != team)
  }

  def legs(team: Team): Int = {
    require(team1 == team || team2== team)

    //  2* win + loose this 3 legs
    2 * plus(team) + matches.count(m => (m.teamWinner != team) && (m.legsAsList.size == 3))
  }
}


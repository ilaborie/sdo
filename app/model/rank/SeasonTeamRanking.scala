package model.rank

import model.orga._
import play.api.cache.Cache
import play.api.Play.current
import model.team.{MatchDetail, TeamChampionship}
import play.api.Logger

/**
 * Season team ranking
 * @param champ championship
 * @param teamRanks team ranking
 */
case class SeasonTeamRanking(champ: TeamChampionship, teamRanks: Seq[TeamRank]) {
  lazy val ordered: Seq[TeamRank] = teamRanks.sortBy(getPosition)

  def getPosition(teamRank: TeamRank): Int = Cache.getOrElse[Int](s"TeamRanking.${champ.season}.team.${teamRank.team.name}") {
    1 + teamRanks.count(_.betterThan(teamRank))
  }
}

object SeasonTeamRanking {

  private val logger = Logger("teamRank")

  def apply(champ: TeamChampionship): SeasonTeamRanking = {
    val ranks = for (team <- Ligue.teams) yield buildRank(champ, team)
    SeasonTeamRanking(champ, ranks)
  }

  private def buildRank(champ: TeamChampionship, team: Team) = {
    val teamMatches: List[MatchDetail] = for {
      day <- champ.days
      ms <- day.matches
      detail <- ms.detail
      if ms.applyTo(team)
    } yield detail
    logger.debug(s"find ${teamMatches.size} for $team")

    TeamRank(team, teamMatches)
  }
}


/**
 * Team ranking
 * @param team team
 * @param win win
 * @param loose loose
 * @param draw draw
 * @param fail fail
 * @param plus plus
 * @param minus minus
 */
case class TeamRank(team: Team, win: Int, loose: Int, draw: Int, fail: Int = 0, plus: Int, minus: Int) {
  val days: Int = win + loose + draw + fail
  val points: Int = 3 * win + 2 * draw + loose
  val diff: Int = plus - minus

  def betterThan(other: TeamRank): Boolean = (this.points > other.points) || (
    (this.points == other.points) && (this.diff > other.diff))
}

object TeamRank {
  def apply(team: Team, details: List[MatchDetail]): TeamRank = {
    val win = details.count(_.win(team))
    val loose = details.count(_.loose(team))
    val draw = details.count(_.draw(team))
    val fail = details.count(_.fail(team))
    val plus = details.map(_.plus(team)).sum
    val minus = details.map(_.minus(team)).sum

    TeamRank(team, win = win, loose = loose, draw = draw, fail = fail, plus = plus, minus = minus)
  }
}

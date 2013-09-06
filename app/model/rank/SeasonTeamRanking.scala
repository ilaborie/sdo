package model.rank

import play.api.Logger

import model.orga._
import model.team._

/**
 * Season team ranking
 * @param champ championship
 * @param teamRanks team ranking
 */
case class SeasonTeamRanking(champ: TeamChampionship, teamRanks: Seq[TeamRank]) {
  lazy val ordered: Seq[TeamRank] = teamRanks.sortBy(sorter)

  private def sorter(rank: TeamRank) = (getPosition(rank), rank.team.toString)

  private val cache = collection.mutable.Map[TeamRank, Int]()
  def getPosition(rank: TeamRank): Int = {
    cache.getOrElseUpdate(rank, 1 + teamRanks.count(_.betterThan(rank)))
  }
}

object SeasonTeamRanking {

  private val logger = Logger("teamRank")

  def apply(season: Season, ligue: Ligue): SeasonTeamRanking = {
    val champ = TeamChampionship(season, ligue)
    val ranks = for (team <- Ligue.teams if !team.omit) yield buildRank(champ, team)
    SeasonTeamRanking(champ, ranks)
  }

  def apply(season: Season, comite: Comite): SeasonTeamRanking = {
    val champ = TeamChampionship(season, comite)
    val ranks = for (team <- comite.teams if !team.omit) yield buildRank(champ, team)
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

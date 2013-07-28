package model.rank

import model.orga._

/**
 * Season team ranking
 * @param season season
 * @param teamRanks team ranking
 */
case class SeasonTeamRanking(season: Season, teamRanks: Seq[TeamRank]) {
  lazy val ordered: Seq[TeamRank] = teamRanks.sortBy(getPosition)

  def getPosition(teamRank: TeamRank): Int = {
    // FIXME Cache data
    1 + teamRanks.count(_.betterThan(teamRank))
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

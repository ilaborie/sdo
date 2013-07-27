package model.rank

import model.orga._

case class SeasonRanking(season: Season, teamRanks: Seq[TeamRank]) {
  lazy val ordered: Seq[TeamRank] = teamRanks.sortBy(getPosition)

  def getPosition(teamRank: TeamRank): Int = {
    // FIXME Cache data
    1 + teamRanks.count(_.betterThan(teamRank))
  }
}

object SeasonRanking {
  // FIXME dummy data

  private val list = List(
    TeamRank(Team.Satanas, win = 9, loose = 0, draw = 0, plus = 133, minus = 47),
    TeamRank(Team.DDD, win = 7, loose = 1, draw = 1, plus = 127, minus = 53),
    TeamRank(Team.X1, win = 7, loose = 2, draw = 0, plus = 119, minus = 61),
    TeamRank(Team.ONeill, win = 5, loose = 3, draw = 1, plus = 93, minus = 58),
    TeamRank(Team.FT, win = 4, loose = 5, draw = 0, plus = 85, minus = 95),
    TeamRank(Team.Coch, win = 2, loose = 5, draw = 1, fail = 1, plus = 62, minus = 98),
    TeamRank(Team.Diabolo, win = 1, loose = 5, draw = 1, fail = 2, plus = 47, minus = 93),
    TeamRank(Team.Wood, win = 1, loose = 5, draw = 1, fail = 1, plus = 55, minus = 105)
  )


  def forComite(comite: Comite) = SeasonRanking("2012-2013", list.filter(_.team.club.comite == comite))

}

case class TeamRank(team: Team, win: Int, loose: Int, draw: Int, fail: Int = 0, plus: Int, minus: Int) {
  val days: Int = win + loose + draw + fail
  val points: Int = 3 * win + 2 * draw + loose
  val diff: Int = plus - minus

  def betterThan(other: TeamRank): Boolean = (this.points > other.points) || (
    (this.points == other.points) && (this.diff > other.diff))

}

package model.rank

import model.orga._
import play.api.cache.Cache
import play.api.Play.current

/**
 * Season Single Ranking
 * @param season season
 * @param ranks ranks
 */
sealed abstract class SeasonParticipantRanking[T <: Participant](season: Season,
                                                                 tournaments: List[Tournament],
                                                                 ranks: Seq[ParticipantRank[T]]) {
  // FIXME require team mapping OK
  lazy val ordered: Seq[ParticipantRank[T]] = ranks.sortBy(getPosition)

  def getPosition(rank: ParticipantRank[T]): Int = Cache.getOrElse[Int](s"player.${rank.participant.name}.position") {
    1 + ranks.count(_.betterThan(rank))
  }
}

/**
 * Single
 * @param season season
 * @param ranks ranks
 */
case class SeasonSingleRanking(season: Season, tournaments: List[Tournament], ranks: Seq[ParticipantRank[Player]])
  extends SeasonParticipantRanking[Player](season, tournaments, ranks)

/**
 * Feminine
 * @param season season
 * @param ranks ranks
 */
case class SeasonFeminineRanking(season: Season, tournaments: List[Tournament], ranks: Seq[ParticipantRank[Player]])
  extends SeasonParticipantRanking[Player](season, tournaments, ranks) {
  require(ranks.filter(!_.participant.feminine).isEmpty, "Que des féminines")
}

/**
 * Junior
 * @param season season
 * @param ranks ranks
 */
case class SeasonJuniorRanking(season: Season, tournaments: List[Tournament], ranks: Seq[ParticipantRank[Player]])
  extends SeasonParticipantRanking[Player](season, tournaments, ranks) {
  require(ranks.filter(!_.participant.junior).isEmpty, "Que des juniors")
}

/**
 * Double
 * @param season season
 * @param ranks ranks
 */
case class SeasonDoubleRanking(season: Season, tournaments: List[Tournament], ranks: Seq[ParticipantRank[Doublette]])
  extends SeasonParticipantRanking[Doublette](season, tournaments, ranks) {
}

/**
 * Participant Rank
 * @param participant participant
 * @param results results
 */
case class ParticipantRank[T <: Participant](participant: T, results: Map[Tournament, TournamentResult]) {

  lazy val points: Int = {
    for ((tournament, result) <- results) yield tournament.getPoint(result)
  }.sum

  def betterSubLevel(rank: ParticipantRank[T]): Boolean = ??? // FIXME implements

  def betterThan(other: ParticipantRank[T]): Boolean = (this.points > other.points) || (
    (this.points == other.points) && betterSubLevel(other))
}


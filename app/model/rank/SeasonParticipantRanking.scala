package model.rank

import model.orga._

/**
 * Season Single Ranking
 * @param season season
 * @param ranks ranks
 */
sealed abstract class SeasonParticipantRanking[T <: Participant](season: Season,
                                                                 tournaments: List[Tournament],
                                                                 ranks: Seq[ParticipantRank[T]]) {
  lazy val ordered: Seq[ParticipantRank[T]] = ranks.sortBy(getPosition)

  // FIXME Cache
  def getPosition(rank: ParticipantRank[T]): Int = {
    1 + ranks.count(_.betterThan(rank))
  }
}

/**
 * Single
 * @param season season
 * @param ranks ranks
 */
case class SeasonSingleRanking[T <: Player](season: Season, tournaments: List[Tournament], ranks: Seq[ParticipantRank[T]])
  extends SeasonParticipantRanking[T](season, tournaments, ranks) {
}

object SeasonSingleRanking {
  def apply(season: Season, ligue: Ligue): SeasonSingleRanking[LicensedPlayer] = {
    val tournaments = ligue.tournaments.filter(!_.isTeam)
    val ranks = for {
      player <- ligue.players
      if player.men
    } yield ParticipantRank(player, TournamentResultData.createResult(player, tournaments))
    SeasonSingleRanking(season, tournaments, ranks.filter(_.points > 0))
  }
}

/**
 * Ladies
 * @param season season
 * @param ranks ranks
 */
case class SeasonLadiesRanking[T <: Player](season: Season, tournaments: List[Tournament], ranks: Seq[ParticipantRank[T]])
  extends SeasonParticipantRanking[T](season, tournaments, ranks) {
  require(ranks.filter(!_.participant.lady).isEmpty, "Only Ladies")
}

object SeasonLadiesRanking {
  def apply(season: Season, ligue: Ligue): SeasonLadiesRanking[LicensedPlayer] = {
    val tournaments = ligue.tournaments.filter(!_.isTeam)
    val ranks = for {
      player <- ligue.players
      if player.lady
    } yield ParticipantRank(player, TournamentResultData.createResult(player, tournaments))
    SeasonLadiesRanking(season, tournaments, ranks.filter(_.points > 0))
  }
}

/**
 * Junior
 * @param season season
 * @param ranks ranks
 */
case class SeasonYouthRanking[T <: Player](season: Season, tournaments: List[Tournament], ranks: Seq[ParticipantRank[T]])
  extends SeasonParticipantRanking[T](season, tournaments, ranks) {
  require(ranks.filter(!_.participant.youth).isEmpty, "Only Youth")
}

object SeasonYouthRanking {
  def apply(season: Season, ligue: Ligue): SeasonYouthRanking[LicensedPlayer] = {
    val tournaments = ligue.tournaments.filter(!_.isTeam)
    val ranks = for {
      player <- ligue.players
      if player.youth
    } yield ParticipantRank(player, TournamentResultData.createResult(player, tournaments))
    SeasonYouthRanking(season, tournaments, ranks.filter(_.points > 0))
  }
}

/**
 * Double
 * @param season season
 * @param ranks ranks
 */
case class SeasonPairsRanking(season: Season, tournaments: List[Tournament], ranks: Seq[ParticipantRank[Pair]])
  extends SeasonParticipantRanking[Pair](season, tournaments, ranks) {
}

object SeasonPairsRanking {
  def apply(season: Season, ligue: Ligue): SeasonPairsRanking = {
    val tournaments = ligue.tournaments.filter(!_.isTeam)
    val pairs = for {
      tour <- tournaments
      pair <- tour.getPairs
    } yield pair

    val ranks: Seq[ParticipantRank[Pair]] = for {
      pair <- pairs
    } yield ParticipantRank(pair, TournamentResultData.createResult(pair, tournaments))
    SeasonPairsRanking(season, tournaments, ranks.filter(_.points > 0))
  }
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

  def betterSubLevel(rank: ParticipantRank[T]): Boolean = {
    // iterate throw Tournaments sorted by TournamentRank
    // : Map[TournamentRank, List[TournamentResult]]
    // getPositions List
    // Compare List

    // FIXME implements
    false
  }

  def betterThan(other: ParticipantRank[T]): Boolean = (this.points > other.points) || (
    (this.points == other.points) && betterSubLevel(other))
}


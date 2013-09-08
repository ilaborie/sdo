package model.rank

import model.orga._

/**
 * Season Single Ranking
 * @param season season
 * @param ranks ranks
 */
sealed abstract class SeasonParticipantRanking(season: Season, ranks: Seq[ParticipantRank]) {

  def tournaments: List[Tournament]

  lazy val ordered = ranks.sortBy(sorter)

  private def sorter(rank: ParticipantRank) = (getPosition(rank), rank.participant.toString)

  private val cache = collection.mutable.Map[ParticipantRank, Int]()

  def getPosition(rank: ParticipantRank): Int = {
    cache.getOrElseUpdate(rank, 1 + ranks.count(_.betterThan(rank)))
  }
}

/**
 * Single
 * @param season season
 * @param ranks ranks
 */
case class SeasonSingleRanking(season: Season, tournaments: List[Tournament], ranks: Seq[ParticipantRank])
  extends SeasonParticipantRanking(season, ranks) {
}

object SeasonSingleRanking {

  def apply(season: Season, rankType: RankingType, players: List[Player], tournaments: List[Tournament]): SeasonSingleRanking = {
    val ranks = for {
      player <- players
      if rankType.canParticipate(player)
    } yield ParticipantRank(player, TournamentResultData.createResult(rankType, player, tournaments))
    SeasonSingleRanking(season, tournaments, ranks.filter(_.points > 0))
  }

  def apply(season: Season, ligue: Ligue): SeasonSingleRanking = {
    val tournaments = ligue.tournaments.filter(!_.isTeam)
    SeasonSingleRanking(season, MensLicensied(ligue), ligue.players.toList, tournaments)
  }

  def apply(season: Season, comite: Comite): SeasonSingleRanking = {
    val tournaments = comite.tournaments
    val players = comite.players ++ Ligue.nlPlayers
    SeasonSingleRanking(season, Single(comite), players.toList, tournaments)
  }
}

/**
 * Ladies
 * @param season season
 * @param ranks ranks
 */
case class SeasonLadiesRanking(season: Season, tournaments: List[Tournament], ranks: Seq[ParticipantRank])
  extends SeasonParticipantRanking(season, ranks)
object SeasonLadiesRanking {

  def apply(season: Season, rankType: RankingType, players: List[Player], tournaments: List[Tournament]): SeasonLadiesRanking = {
    val ranks = for {
      player <- players
      if rankType.canParticipate(player)
    } yield ParticipantRank(player, TournamentResultData.createResult(rankType, player, tournaments))
    SeasonLadiesRanking(season, tournaments, ranks.filter(_.points > 0))
  }

  def apply(season: Season, ligue: Ligue): SeasonLadiesRanking = {
    val tournaments = ligue.tournaments.filter(!_.isTeam)
    SeasonLadiesRanking(season, LadiesLicensied(ligue), ligue.players.toList, tournaments)
  }

  def apply(season: Season, comite: Comite): SeasonLadiesRanking = {
    val tournaments = comite.tournaments
    val players = comite.players ++ Ligue.nlPlayers
    SeasonLadiesRanking(season, Ladies(comite), players.toList, tournaments)
  }
}

/**
 * Junior
 * @param season season
 * @param ranks ranks
 */
case class SeasonYouthRanking(season: Season, tournaments: List[Tournament], ranks: Seq[ParticipantRank])
  extends SeasonParticipantRanking(season, ranks)

object SeasonYouthRanking {

  def apply(season: Season, rankType: RankingType, players: List[Player], tournaments: List[Tournament]): SeasonYouthRanking = {
    val ranks = for {
      player <- players
      if rankType.canParticipate(player)
    } yield ParticipantRank(player, TournamentResultData.createResult(rankType, player, tournaments))
    SeasonYouthRanking(season, tournaments, ranks.filter(_.points > 0))
  }

  def apply(season: Season, ligue: Ligue): SeasonYouthRanking = {
    val tournaments = ligue.tournaments.filter(!_.isTeam)
    SeasonYouthRanking(season, YouthLicensied(ligue), ligue.players.toList, tournaments)
  }

  def apply(season: Season, comite: Comite): SeasonYouthRanking = {
    val tournaments = comite.tournaments
    val players = comite.players ++ Ligue.nlPlayers
    SeasonYouthRanking(season, Youth(comite), players.toList, tournaments)
  }
}

/**
 * Double
 * @param season season
 * @param ranks ranks
 */
case class SeasonPairsRanking(season: Season, tournaments: List[Tournament], ranks: Seq[ParticipantRank])
  extends SeasonParticipantRanking(season, ranks) {
}

object SeasonPairsRanking {

  def apply(season: Season, rankType: RankingType, pairs: List[Pair], tournaments: List[Tournament]): SeasonPairsRanking = {
    val ranks = for {
      pair <- pairs
      if rankType.canParticipate(pair)
    } yield ParticipantRank(pair, TournamentResultData.createResult(rankType, pair, tournaments))
    SeasonPairsRanking(season, tournaments, ranks.filter(_.points > 0))
  }

  def apply(season: Season, ligue: Ligue): SeasonPairsRanking = {
    val tournaments = ligue.tournaments.filter(!_.isTeam)
    val pairs = {
      for {
        tour <- tournaments
        pair <- tour.getPairs
      } yield pair
    }.toSet.toList

    SeasonPairsRanking(season, PairsLicensied(ligue), pairs, tournaments)
  }

  def apply(season: Season, comite: Comite): SeasonPairsRanking = {
    val tournaments = comite.tournaments
    val pairs = {
      for {
        tour <- tournaments
        pair <- tour.getPairs
      } yield pair
    }.toSet.toList
    SeasonPairsRanking(season, Pairs(comite), pairs, tournaments)
  }
}

/**
 * Participant Rank
 * @param participant participant
 * @param results results
 */
case class ParticipantRank(participant: Participant, results: Map[Tournament, TournamentResult]) {

  lazy val points: Int = {
    for ((tournament, result) <- results) yield tournament.getPoint(result)
  }.sum

  def betterSubLevel(rank: ParticipantRank): Boolean = {
    // iterate throw Tournaments sorted by TournamentRank
    // : Map[TournamentRank, List[TournamentResult]]
    // getPositions List
    // Compare List

    // FIXME implements
    false
  }

  def betterThan(other: ParticipantRank): Boolean = (this.points > other.points) || (
    (this.points == other.points) && betterSubLevel(other))
}


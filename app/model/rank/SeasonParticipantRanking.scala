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
  lazy val ordered: Seq[ParticipantRank[T]] = ranks.sortBy(sorter)

  private def sorter(rank: ParticipantRank[T]) = (getPosition(rank), rank.participant.toString)

  private val cache = collection.mutable.Map[ParticipantRank[T], Int]()

  def getPosition(rank: ParticipantRank[T]): Int = {
    cache.getOrElseUpdate(rank, 1 + ranks.count(_.betterThan(rank)))
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
    val rankType = MensLicensied(ligue)
    val ranks = for {
      player <- ligue.players
      if rankType.canParticipate(player)
    } yield ParticipantRank(player, TournamentResultData.createResult(rankType, player, tournaments))
    SeasonSingleRanking(season, tournaments, ranks.filter(_.points > 0))
  }

  def apply(season: Season, comite: Comite): SeasonSingleRanking[Player] = {
    val tournaments = comite.tournaments
    val players: Seq[Player] = comite.players ++ Ligue.nlPlayers
    val rankType = Single(comite)
    val ranks = for {
      player <- players
      if rankType.canParticipate(player)
    } yield ParticipantRank(player, TournamentResultData.createResult(rankType, player, tournaments))
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
    val rankType = LadiesLicensied(ligue)
    val ranks = for {
      player <- ligue.players
      if rankType.canParticipate(player)
    } yield ParticipantRank(player, TournamentResultData.createResult(rankType, player, tournaments))
    SeasonLadiesRanking(season, tournaments, ranks.filter(_.points > 0))
  }

  def apply(season: Season, comite: Comite): SeasonLadiesRanking[Player] = {
    val tournaments = comite.tournaments
    val players: Seq[Player] = comite.players ++ Ligue.nlPlayers
    val rankType = Ladies(comite)
    val ranks = for {
      player <- players
      if rankType.canParticipate(player)
    } yield ParticipantRank(player, TournamentResultData.createResult(rankType, player, tournaments))
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
    var rankType = YouthLicensied(ligue)
    val ranks = for {
      player <- ligue.players
      if rankType.canParticipate(player)
    } yield ParticipantRank(player, TournamentResultData.createResult(rankType, player, tournaments))
    SeasonYouthRanking(season, tournaments, ranks.filter(_.points > 0))
  }

  def apply(season: Season, comite: Comite): SeasonYouthRanking[Player] = {
    val tournaments = comite.tournaments
    val players: Seq[Player] = comite.players ++ Ligue.nlPlayers
    var rankType = Youth(comite)
    val ranks = for {
      player <- players
      if rankType.canParticipate(player)
    } yield ParticipantRank(player, TournamentResultData.createResult(rankType, player, tournaments))
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
    var rankType = PairsLicensied(ligue)
    val ranks: Seq[ParticipantRank[Pair]] = for {
      tour <- tournaments
      pair <- tour.getPairs
      if rankType.canParticipate(pair)
    } yield ParticipantRank(pair, TournamentResultData.createResult(rankType, pair, tournaments))
    SeasonPairsRanking(season, tournaments, ranks.filter(_.points > 0))
  }

  def apply(season: Season, comite: Comite): SeasonPairsRanking = {
    val tournaments = comite.tournaments
    var rankType = Pairs(comite)
    val ranks: Seq[ParticipantRank[Pair]] = for {
      tour <- tournaments
      pair <- tour.getPairs
      if rankType.canParticipate(pair)
    } yield ParticipantRank(pair, TournamentResultData.createResult(rankType, pair, tournaments))
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


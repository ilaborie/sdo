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

package model.rank

import model.orga._

/**
 * Season Single Ranking
 * @param season season
 * @param ranks ranks
 */
sealed abstract class SeasonParticipantRanking(season: Season, ranks: Seq[ParticipantRank]) {

  def tournaments: Seq[Tournament]

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
case class SeasonSingleRanking(season: Season, tournaments: Seq[Tournament], ranks: Seq[ParticipantRank])
  extends SeasonParticipantRanking(season, ranks) {
}

object SeasonSingleRanking {

  def apply(season: Season, rankType: RankingType, players: Seq[Player], tournaments: Seq[Tournament]): SeasonSingleRanking = {
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
    val tournaments = for {
      c <- comite.ligue.comites
      tournament <- c.tournaments
    } yield tournament
    val players = comite.players ++ Ligue.nlPlayers
    SeasonSingleRanking(season, Single(comite), players.toList, tournaments)
  }
}

/**
 * Ladies
 * @param season season
 * @param ranks ranks
 */
case class SeasonLadiesRanking(season: Season, tournaments: Seq[Tournament], ranks: Seq[ParticipantRank])
  extends SeasonParticipantRanking(season, ranks)

object SeasonLadiesRanking {

  def apply(season: Season, rankType: RankingType, players: Seq[Player], tournaments: Seq[Tournament]): SeasonLadiesRanking = {
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
    val tournaments = for {
      c <- comite.ligue.comites
      tournament <- c.tournaments
    } yield tournament
    val players = comite.players ++ Ligue.nlPlayers
    SeasonLadiesRanking(season, Ladies(comite), players.toList, tournaments)
  }
}

/**
 * Junior
 * @param season season
 * @param ranks ranks
 */
case class SeasonYouthRanking(season: Season, tournaments: Seq[Tournament], ranks: Seq[ParticipantRank])
  extends SeasonParticipantRanking(season, ranks)

object SeasonYouthRanking {

  def apply(season: Season, rankType: RankingType, players: Seq[Player], tournaments: Seq[Tournament]): SeasonYouthRanking = {
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
    val tournaments = for {
      c <- comite.ligue.comites
      tournament <- c.tournaments
    } yield tournament
    val players = comite.players ++ Ligue.nlPlayers
    SeasonYouthRanking(season, Youth(comite), players.toList, tournaments)
  }
}

/**
 * Double
 * @param season season
 * @param ranks ranks
 */
case class SeasonPairsRanking(season: Season, tournaments: Seq[Tournament], ranks: Seq[ParticipantRank])
  extends SeasonParticipantRanking(season, ranks) {
}

object SeasonPairsRanking {

  def apply(season: Season, rankType: RankingType, pairs: Seq[Pair], tournaments: Seq[Tournament]): SeasonPairsRanking = {
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
    val tournaments = for {
      c <- comite.ligue.comites
      tournament <- c.tournaments
    } yield tournament
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


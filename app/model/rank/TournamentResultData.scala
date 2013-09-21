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

import java.util.{List => JavaList, Map => JavaMap}

import scala.collection.JavaConversions._

import play.api.Logger

import model.orga._
import util.YamlParser

/**
 * Helper to read Tournament result
 */
object TournamentResultData {
  private val logger = Logger("data")

  def readBaseTournamentResult(file: String) = {
    logger.info(s"Read result in $file")
    YamlParser.tryParseFile(file) match {
      case None => (None, None, None, None)
      case Some(x) => {
        logger.trace(s"Read $x")
        val info: Map[String, Any] = x.asInstanceOf[JavaMap[String, Any]].toMap

        val mens = TournamentResultData.toTournamentResults("mens", info)
        val ladies = TournamentResultData.toTournamentResults("ladies", info)
        val youth = TournamentResultData.toTournamentResults("youth", info)
        val pairs = TournamentResultData.toTournamentPairResults("pairs", info)
        (mens, ladies, youth, pairs)
      }
    }
  }

  /**
   * Create result
   * @param rankType ranking type
   * @param player player
   * @param tournaments tournaments
   * @return result
   */
  def createResult[T <: Participant](rankType: RankingType, player: T, tournaments: Seq[Tournament]): Map[Tournament, TournamentResult] = {
    for (tournament <- tournaments) yield (tournament, createResult(rankType, player, tournament))
  }.toMap

  /**
   * Create result
   * @param player player
   * @param tournament tournament
   * @return result
   */
  def createResult[T <: Participant](rankType: RankingType, player: T, tournament: Tournament): TournamentResult = tournament match {
    case tour: BaseTournament => createBaseTournamentResult(rankType, player, tour).getOrElse(NoParticipation)
    case ic: ComiteRank => createComiteRankResult(rankType, player, ic).getOrElse(NoParticipation)
    case nt: NationalTournament => createNationalTournamentResult(rankType, player, nt).getOrElse(NoParticipation)
    case _ => throw new IllegalStateException(s"Cannot find result of $tournament")
  }

  private def createBaseTournamentResult(rankType: RankingType, player: Participant, tour: BaseTournament): Option[TournamentResult] = {
    rankType match {
      case _: Single => if (tour.mens.isDefined) tour.mens.get.getResult(player) else None
      case _: SingleLicensied => if (tour.mens.isDefined) tour.mens.get.getResult(player) else None
      case _: Mens => if (tour.mens.isDefined) tour.mens.get.getResult(player) else None
      case _: MensLicensied => if (tour.mens.isDefined) tour.mens.get.getResult(player) else None
      case _: Ladies => if (tour.ladies.isDefined) tour.ladies.get.getResult(player) else None
      case _: LadiesLicensied => if (tour.ladies.isDefined) tour.ladies.get.getResult(player) else None
      case _: Youth => if (tour.youth.isDefined) tour.youth.get.getResult(player) else None
      case _: YouthLicensied => if (tour.youth.isDefined) tour.youth.get.getResult(player) else None
      case _: Pairs => if (tour.pairs.isDefined) tour.pairs.get.getResult(player) else None
      case _: PairsLicensied => if (tour.pairs.isDefined) tour.pairs.get.getResult(player) else None
    }
  }

  private def createComiteRankResult(rankType: RankingType, player: Participant, ic: ComiteRank): Option[TournamentResult] = {
    val ligue = ic.ligue
    rankType match {
      case _: Single => createRankResult(InterComiteRanking.single(ligue), player.asInstanceOf[LicensedPlayer])
      case _: SingleLicensied => createRankResult(InterComiteRanking.single(ligue), player.asInstanceOf[LicensedPlayer])
      case _: Mens => createRankResult(InterComiteRanking.single(ligue), player.asInstanceOf[LicensedPlayer])
      case _: MensLicensied => createRankResult(InterComiteRanking.single(ligue), player.asInstanceOf[LicensedPlayer])
      case _: Ladies => createRankResult(InterComiteRanking.ladies(ligue), player.asInstanceOf[LicensedPlayer])
      case _: LadiesLicensied => createRankResult(InterComiteRanking.ladies(ligue), player.asInstanceOf[LicensedPlayer])
      case _: Youth => createRankResult(InterComiteRanking.youth(ligue), player.asInstanceOf[LicensedPlayer])
      case _: YouthLicensied => createRankResult(InterComiteRanking.youth(ligue), player.asInstanceOf[LicensedPlayer])
      case _: Pairs => createRankResult(InterComiteRanking.pairs(ligue), player.asInstanceOf[Pair])
      case _: PairsLicensied => createRankResult(InterComiteRanking.pairs(ligue), player.asInstanceOf[Pair])
    }
  }

  def createRankResult(ranking: SeasonSingleRanking, player: LicensedPlayer): Option[TournamentResult] = {
    ranking.ranks.find(_.participant==player) match {
      case Some(pr) => Some(RoundRobin(ranking.getPosition(pr)))
      case _ => None
    }
  }

  def createRankResult(ranking: SeasonLadiesRanking, player: LicensedPlayer): Option[TournamentResult] = {
    ranking.ranks.find(_.participant==player) match {
      case Some(pr) => Some(RoundRobin(ranking.getPosition(pr)))
      case _ => None
    }
  }

  def createRankResult(ranking: SeasonYouthRanking, player: LicensedPlayer): Option[TournamentResult] = {
    ranking.ranks.find(_.participant==player) match {
      case Some(pr) => Some(RoundRobin(ranking.getPosition(pr)))
      case _ => None
    }
  }

  def createRankResult(ranking: SeasonPairsRanking, player: Pair): Option[TournamentResult] = {
    ranking.ranks.find(_.participant==player) match {
      case Some(pr) => Some(RoundRobin(ranking.getPosition(pr)))
      case _ => None
    }
  }

  /**
   * National result
   * @param tournament tournament
   * @param player player
   * @return result
   */
  private def createNationalTournamentResult(rankType: RankingType, player: Participant, tournament: NationalTournament): Option[WinningMatch] = {
    rankType match {
      case _: Single => tournament.mens.get(player).map(WinningMatch)
      case _: SingleLicensied => tournament.mens.get(player).map(WinningMatch)
      case _: Mens => tournament.mens.get(player).map(WinningMatch)
      case _: MensLicensied => tournament.mens.get(player).map(WinningMatch)
      case _: Ladies => tournament.ladies.get(player).map(WinningMatch)
      case _: LadiesLicensied => tournament.ladies.get(player).map(WinningMatch)
      case _: Youth => tournament.youth.get(player).map(WinningMatch)
      case _: YouthLicensied => tournament.youth.get(player).map(WinningMatch)
      case _: Pairs => tournament.pairs.get(player).map(WinningMatch)
      case _: PairsLicensied => tournament.pairs.get(player).map(WinningMatch)
    }
  }

  /**
   * Lookup for a player
   * @param name the player name
   * @return an optional player
   */
  private def searchPlayer(name: String) = {
    val search = Ligue.findPlayerByName(name)
    if (search.isEmpty) logger.warn(s"Could not find player: $name")
    search
  }

  /**
   * Read player
   * @param info info
   * @param key key
   * @return player or none
   */
  def readPlayer(info: Map[String, Any], key: String): Option[Player] = {
    if (info.contains(key)) {
      val value = info(key)
      if (value == null) None
      else searchPlayer(value.asInstanceOf[String])
    } else None
  }

  /**
   * Read players
   * @param info info
   * @param key key
   * @return array of players
   */
  def readPlayers(info: Map[String, Any], key: String): Seq[Player] = {
    if (info.contains(key)) {
      val value = info(key)
      if (value == null) Nil
      else {
        val names = value.asInstanceOf[JavaList[String]].toList
        for {
          name <- names
          player <- searchPlayer(name)
        } yield player
      }
    } else Nil
  }

  /**
   * Create a pair
   * @param names names
   * @return a pair
   */
  def createPair(names: List[String]): Option[Pair] = {
    if (names.size != 2) None
    else {
      val p1 = searchPlayer(names(0))
      val p2 = searchPlayer(names(1))
      if (p1.isDefined && p2.isDefined) Some(Pair(p1.get, p2.get))
      else None
    }
  }

  /**
   * Read pair
   * @param info info
   * @param key key
   * @return pair
   */
  def readPair(info: Map[String, Any], key: String): Option[Pair] = {
    if (info.contains(key)) {
      val value = info(key)
      if (value == null) None
      else {
        val names = value.asInstanceOf[JavaList[String]].toList.sorted
        createPair(names)
      }
    } else None
  }

  /**
   * Read pairs
   * @param info info
   * @param key key
   * @return results
   */
  def readPairs(info: Map[String, Any], key: String): Seq[Pair] = {
    if (info.contains(key)) {
      val value = info(key)
      if (value == null) Nil
      else {
        val list = value.asInstanceOf[JavaList[JavaList[String]]].toList
        for {
          names <- list
          pair <- createPair(names.toList.sorted)
        } yield pair
      }
    } else Nil
  }

  /**
   * Read a group
   * @param info data
   * @param key group key
   * @return result
   */
  def readPlayerGroup(info: Map[String, Any], key: String): Option[List[Player]] = {
    if (info.contains(key)) {
      val value = info(key)
      if (value == null) None
      else {
        val names = value.asInstanceOf[JavaList[String]].toList
        val res = for {
          name <- names
          player <- searchPlayer(name)
        } yield player
        if (res.isEmpty) None
        else Some(res)
      }
    } else None
  }

  /**
   * Create Player groups
   * @param data data
   * @return groups
   */
  def readPlayerGroups(data: Map[String, Any]): Option[List[List[Player]]] = {
    val res = for {
      index <- 1 to 8
      group <- readPlayerGroup(data, s"group-$index")
    } yield group

    if (res.isEmpty) None
    else Some(res.toList)
  }

  /**
   * Read Tournament results
   * @param key key
   * @param info info
   * @return results
   */
  def toTournamentResults(key: String, info: Map[String, Any]): Option[TournamentResults[Player]] = {
    if (info.contains(key)) {
      val value = info(key)
      if (value == null) None
      else {
        val data = value.asInstanceOf[JavaMap[String, Any]].toMap

        val winner: Option[Player] = readPlayer(data, "winner")
        val runnerUp = readPlayer(data, "runner-up")
        val semiFinal = readPlayers(data, "semi-final")
        val quarterFinal = readPlayers(data, "quater-final")
        val eighthFinal = readPlayers(data, "eighth-final")
        val sixteenthFinal = readPlayers(data, "sixteenth-final")
        val thirtySecondFinal = readPlayers(data, "thritysecond-final")
        val groups: Option[List[List[Player]]] = readPlayerGroups(data)

        Some(TournamentResults[Player](winner, runnerUp, semiFinal, quarterFinal, eighthFinal, sixteenthFinal, thirtySecondFinal, groups))
      }
    }
    else None
  }

  /**
   * Read tournament results
   * @param key key
   * @param info info
   * @return results
   */
  def toTournamentPairResults(key: String, info: Map[String, Any]): Option[TournamentResults[Pair]] = {
    if (info.contains(key)) {
      val value = info(key)
      if (value == null) None
      else {
        val data = value.asInstanceOf[JavaMap[String, Any]].toMap

        val winner = readPair(data, "winner")
        val runnerUp = readPair(data, "runner-up")
        val semiFinal = readPairs(data, "semi-final")
        val quarterFinal = readPairs(data, "quater-final")
        val eighthFinal = readPairs(data, "eighth-final")
        val sixteenthFinal = readPairs(data, "sixteenth-final")
        val thirtySecondFinal = readPairs(data, "thritysecond-final")
        val groups = None // No RoundRobin for pair

        Some(TournamentResults[Pair](winner, runnerUp, semiFinal, quarterFinal, eighthFinal, sixteenthFinal, thirtySecondFinal, groups))
      }
    }
    else None
  }
}

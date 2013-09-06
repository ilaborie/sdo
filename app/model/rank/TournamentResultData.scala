package model.rank

import java.util.{List => JavaList, Map => JavaMap}

import scala.collection.JavaConversions._


import model.orga._

/**
 * Helper to read Tournament result
 */
object TournamentResultData {

  /**
   * Create result
   * @param player player
   * @param tournaments tournaments
   * @return result
   */
  def createResult[T <: Participant](player: T, tournaments: Seq[Tournament]): Map[Tournament, TournamentResult] = {
    for (tournament <- tournaments) yield (tournament, createResult(player, tournament))
  }.toMap

  /**
   * Create result
   * @param player player
   * @param tournament tournament
   * @return result
   */
  def createResult[T <: Participant](player: T, tournament: Tournament): TournamentResult = tournament match {
    case ol: OpenLigue => createOpenLigueResult(player, ol).getOrElse(NoParticipation)
    case cl: CoupeLigue => createCoupeLigueResult(player, cl).getOrElse(NoParticipation)
    case ml: MasterLigue => createMasterLigueResult(player, ml).getOrElse(NoParticipation)
    case cc: CoupeComite => createCoupeComiteResult(player, cc).getOrElse(NoParticipation)
    case oc: OpenClub => createOpenClubResult(player, oc).getOrElse(NoParticipation)
    case ic: ComiteRank => createComiteRankResult(player, ic).getOrElse(NoParticipation)
    case nt: NationalTournament => createNationalTournamentResult(player, nt).getOrElse(NoParticipation)
    case _ => throw new IllegalStateException(s"Cannot find result of $tournament")
  }

  // FIXME Implements
  private def createOpenLigueResult(player: Participant, ol: OpenLigue): Option[TournamentResult] = None

  private def createCoupeLigueResult(player: Participant, cl: CoupeLigue): Option[TournamentResult] = None

  private def createMasterLigueResult(player: Participant, ml: MasterLigue): Option[TournamentResult] = None

  private def createCoupeComiteResult(player: Participant, cc: CoupeComite): Option[TournamentResult] = None

  private def createOpenClubResult(player: Participant, oc: OpenClub): Option[TournamentResult] = {
    player match {
      case p: LicensedPlayer => {
        if (!p.youth && !p.lady && oc.mens.isDefined) oc.mens.get.getResult(p)
        else if (p.lady && !p.youth && oc.ladies.isDefined) oc.ladies.get.getResult(p)
        else if (p.youth && oc.youth.isDefined) oc.youth.get.getResult(p)
        else None
      }
      case d: Pair => if (oc.pairs.isDefined) oc.pairs.get.getResult(d) else None
      case _ => None
    }
  }

  private def createComiteRankResult(player: Participant, ic: ComiteRank): Option[TournamentResult] = None

  /**
   * National result
   * @param tournament tournament
   * @param player player
   * @return result
   */
  private def createNationalTournamentResult(player: Participant, tournament: NationalTournament): Option[WinningMatch] = {
    player match {
      case p: LicensedPlayer => {
        if (!p.youth && !p.lady) tournament.mens.get(p).map(WinningMatch)
        else if (p.lady && !p.youth) tournament.ladies.get(p).map(WinningMatch)
        else /* if (p.youth ) */ tournament.youth.get(p).map(WinningMatch)
      }
      case d: Pair => tournament.pairs.get(d).map(WinningMatch)
      case _ => None
    }
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
      else {
        val name = value.asInstanceOf[String]
        Ligue.findPlayerByName(name)
      }
    } else None
  }

  /**
   * Read players
   * @param info info
   * @param key key
   * @return array of players
   */
  def readPlayers(info: Map[String, Any], key: String): Array[Player] = {
    if (info.contains(key)) {
      val value = info(key)
      if (value == null) Array()
      else {
        val names = value.asInstanceOf[JavaList[String]].toList
        val lst = for {
          name <- names
          player <- Ligue.findPlayerByName(name)
        } yield player
        lst.toArray
      }
    } else Array()
  }

  /**
   * Create a pair
   * @param names names
   * @return a pair
   */
  def createPair(names: List[String]): Option[Pair] = {
    if (names.size != 2) None
    else {
      val p1 = Ligue.findPlayerByName(names(1))
      val p2 = Ligue.findPlayerByName(names(2))
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
  def readPairs(info: Map[String, Any], key: String): Array[Pair] = {
    if (info.contains(key)) {
      val value = info(key)
      if (value == null) Array()
      else {
        val list = value.asInstanceOf[JavaList[JavaList[String]]].toList
        val lst = for {
          names <- list
          pair <- createPair(names.toList.sorted)
        } yield pair
        lst.toArray
      }
    } else Array()
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
          player <- Ligue.findPlayerByName(name)
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

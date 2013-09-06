package model.rank

import model.orga._

sealed abstract class RankingType {
  def canParticipate(player: Participant): Boolean
}

case class Single(comite: Comite) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case _: NotLicensedPlayer => true
    case lp: LicensedPlayer => comite.players.contains(lp)
    case _ => false
  }
}

case class SingleLicensied(orga: PlayerContainer) extends RankingType {
  def canParticipate(player: Participant): Boolean = orga.isMember(player)
}

case class Mens(comite: Comite) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case p: NotLicensedPlayer => !p.lady
    case p: LicensedPlayer => !p.lady && comite.players.contains(p)
    case _ => false
  }
}

case class MensLicensied(orga: PlayerContainer) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case p: Player => !p.lady && orga.isMember(player)
    case _ => false
  }
}

case class Ladies(comite: Comite) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case p: NotLicensedPlayer => p.lady
    case p: LicensedPlayer => p.lady && comite.players.contains(p)
    case _ => false
  }
}

case class LadiesLicensied(orga: PlayerContainer) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case p: Player => p.lady && orga.isMember(player)
    case _ => false
  }
}

case class Youth(comite: Comite) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case p: NotLicensedPlayer => p.youth
    case p: LicensedPlayer => p.youth && comite.players.contains(p)
    case _ => false
  }
}

case class YouthLicensied(orga: PlayerContainer) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case p: Player => p.youth && orga.isMember(player)
    case _ => false
  }
}

case class Pairs(comite: Comite) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case p: Pair => comite.isMember(p)
    case _ => false
  }
}

case class PairsLicensied(orga: PlayerContainer) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case p: Pair => orga.isMember(player)
    case _ => false
  }
}


object ComiteRanking {

  val season = Season.currentSeason

  def qualifyForMasterSingle(position: Int) = position <= 6

  def qualifyForMasterLadies(position: Int) = position <= 3

  def qualifyForMasterYouth(position: Int) = position <= 3

  def qualifyForMasterPairs(position: Int) = position <= 3


  def single(comite: Comite) = SeasonSingleRanking(season, comite)

  def ladies(comite: Comite) = SeasonLadiesRanking(season, comite)

  def youth(comite: Comite) = SeasonYouthRanking(season, comite)

  def pairs(comite: Comite) = SeasonPairsRanking(season, comite)

  def team(comite: Comite) = SeasonTeamRanking(season, comite)

}

object InterComiteRanking {

  val season = Season.currentSeason

  private def getInterComiteTournaments(ligue: Ligue) = {
    for {
      comite <- ligue.comites
      tournament <- comite.tournaments
    } yield tournament
  }.toList

  def single(ligue: Ligue) =
    SeasonSingleRanking(season, MensLicensied(ligue), ligue.players.toList, getInterComiteTournaments(ligue))

  def ladies(ligue: Ligue) =
    SeasonLadiesRanking(season, LadiesLicensied(ligue), ligue.players.toList, getInterComiteTournaments(ligue))

  def youth(ligue: Ligue) =
    SeasonYouthRanking(season, YouthLicensied(ligue), ligue.players.toList, getInterComiteTournaments(ligue))

  def pairs(ligue: Ligue) = {
    val tournaments = getInterComiteTournaments(ligue)
    val pairs = {
      for {
        tour <- tournaments
        pair <- tour.getPairs
      } yield pair
    }.toSet.toList
    SeasonPairsRanking(season, PairsLicensied(ligue), pairs, tournaments)
  }

  def team(ligue: Ligue) = SeasonTeamRanking(season, ligue)

}

object LigueRanking {

  val season = Season.currentSeason

  def qualifyForMasterSingle(position: Int) = position <= 4

  def qualifyForMasterLadies(position: Int) = position <= 2

  def qualifyForMasterYouth(position: Int) = position <= 2

  def qualifyForMasterPairs(position: Int) = position <= 2

  def single(ligue: Ligue) = SeasonSingleRanking(season, ligue)

  def ladies(ligue: Ligue) = SeasonLadiesRanking(season, ligue)

  def youth(ligue: Ligue) = SeasonYouthRanking(season, ligue)

  def pairs(ligue: Ligue) = SeasonPairsRanking(season, ligue)

}


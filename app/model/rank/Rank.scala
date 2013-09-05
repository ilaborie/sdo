package model.rank

import model.orga._


object ComiteRanking {

  val season = Season.currentSeason

  def qualifyForMasterSingle(position:Int) = position<=6
  def qualifyForMasterLadies(position:Int) = position<=3
  def qualifyForMasterYouth(position:Int) = position<=3
  def qualifyForMasterPairs(position:Int) = position<=3


  // FIXME dummy data
  def single(comite: Comite) = SeasonSingleRanking[Player](season, comite.tournaments, Nil)

  def ladies(comite: Comite) = SeasonLadiesRanking[Player](season, comite.tournaments, Nil)

  def youth(comite: Comite) = SeasonYouthRanking[Player](season, comite.tournaments, Nil)

  def pairs(comite: Comite) = SeasonPairsRanking(season, comite.tournaments, Nil)

  def team(comite: Comite) = SeasonTeamRanking(season, comite)

}

object InterComiteRanking {
  // FIXME dummy data

  val season = Season.currentSeason

  private def getInterComiteTournaments(ligue: Ligue) = {
    for {
      comite <- ligue.comites
      tournament <- comite.tournaments
    } yield tournament
  }.toList

  def single(ligue: Ligue) = SeasonSingleRanking[LicensedPlayer](season, getInterComiteTournaments(ligue), Nil)

  def ladies(ligue: Ligue) = SeasonLadiesRanking[LicensedPlayer](season, getInterComiteTournaments(ligue), Nil)

  def youth(ligue: Ligue) = SeasonYouthRanking[LicensedPlayer](season, getInterComiteTournaments(ligue), Nil)

  def pairs(ligue: Ligue) = SeasonPairsRanking(season, getInterComiteTournaments(ligue), Nil)

}

object LigueRanking {

  val season = Season.currentSeason

  def qualifyForMasterSingle(position:Int) = position<=4
  def qualifyForMasterLadies(position:Int) = position<=2
  def qualifyForMasterYouth(position:Int) = position<=2
  def qualifyForMasterPairs(position:Int) = position<=2

  def single(ligue: Ligue) = SeasonSingleRanking(season, ligue)

  def ladies(ligue: Ligue) = SeasonLadiesRanking(season, ligue)

  def youth(ligue: Ligue) = SeasonYouthRanking(season, ligue)

  // FIXME
  def pairs(ligue: Ligue) = SeasonPairsRanking(season, ligue.tournaments.filter(!_.isTeam), Nil)

  def team(ligue: Ligue) =    SeasonTeamRanking(season, ligue)

}


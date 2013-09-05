package model.rank

import model.orga._


object ComiteRanking {
  // FIXME dummy data

  val season = Season.currentSeason

  def single(comite: Comite) = SeasonSingleRanking(season, comite.tournaments, Nil)

  def feminine(comite: Comite) = SeasonFeminineRanking(season, comite.tournaments, Nil)

  def junior(comite: Comite) = SeasonJuniorRanking(season, comite.tournaments, Nil)

  def double(comite: Comite) = SeasonDoubleRanking(season, comite.tournaments, Nil)

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

  def single(ligue: Ligue) = SeasonSingleRanking(season, getInterComiteTournaments(ligue), Nil)

  def feminine(ligue: Ligue) = SeasonFeminineRanking(season, getInterComiteTournaments(ligue), Nil)

  def junior(ligue: Ligue) = SeasonJuniorRanking(season, getInterComiteTournaments(ligue), Nil)

  def double(ligue: Ligue) = SeasonDoubleRanking(season, getInterComiteTournaments(ligue), Nil)

}

object LigueRanking {
  // FIXME dummy data

  val season = Season.currentSeason

  def single(ligue: Ligue) = SeasonSingleRanking(season, ligue.tournaments.filter(!_.isTeam), Nil)

  def feminine(ligue: Ligue) = SeasonFeminineRanking(season, ligue.tournaments.filter(!_.isTeam), Nil)

  def junior(ligue: Ligue) = SeasonJuniorRanking(season, ligue.tournaments.filter(!_.isTeam), Nil)

  def double(ligue: Ligue) = SeasonDoubleRanking(season, ligue.tournaments.filter(!_.isTeam), Nil)

  def team(ligue: Ligue) = SeasonTeamRanking(season, ligue)

}


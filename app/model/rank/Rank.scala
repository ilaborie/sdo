package model.rank

import model.orga._


object ComiteRanking {
  // FIXME dummy data

  val season = Data.currentSeason

  def single(comite: Comite) = SeasonSingleRanking(season, comite.tournaments, Nil)

  def feminine(comite: Comite) = SeasonFeminineRanking(season, comite.tournaments, Nil)

  def junior(comite: Comite) = SeasonJuniorRanking(season, comite.tournaments, Nil)

  def double(comite: Comite) = SeasonDoubleRanking(season, comite.tournaments, Nil)

  def team(comite: Comite) = SeasonTeamRanking(season, comite)

}

object LigueRanking {
  // FIXME dummy data

  val season = Data.currentSeason

  def single(ligue: Ligue) = SeasonSingleRanking(Data.currentSeason, ligue.tournaments, Nil)

  def feminine(ligue: Ligue) = SeasonFeminineRanking(Data.currentSeason, ligue.tournaments, Nil)

  def junior(ligue: Ligue) = SeasonJuniorRanking(Data.currentSeason, ligue.tournaments, Nil)

  def double(ligue: Ligue) = SeasonDoubleRanking(Data.currentSeason, ligue.tournaments, Nil)

  def team(ligue: Ligue) = SeasonTeamRanking(season, ligue)

}


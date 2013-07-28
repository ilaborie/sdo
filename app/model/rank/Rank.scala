package model.rank

import model.orga._


object ComiteRanking {
  // FIXME dummy data

  def single(comite: Comite) = SeasonSingleRanking(Data.currentSeason, comite.tournaments, Nil)

  def feminine(comite: Comite) = SeasonFeminineRanking(Data.currentSeason, comite.tournaments, Nil)

  def junior(comite: Comite) = SeasonJuniorRanking(Data.currentSeason, comite.tournaments, Nil)

  def double(comite: Comite) = SeasonDoubleRanking(Data.currentSeason, comite.tournaments, Nil)

  def team(comite: Comite) = SeasonTeamRanking(Data.currentSeason, Nil)

}

object LigueRanking {
  // FIXME dummy data

  def single(ligue: Ligue) = SeasonSingleRanking(Data.currentSeason, ligue.tournaments, Nil)

  def feminine(ligue: Ligue) = SeasonFeminineRanking(Data.currentSeason, ligue.tournaments, Nil)

  def junior(ligue: Ligue) = SeasonJuniorRanking(Data.currentSeason, ligue.tournaments, Nil)

  def double(ligue: Ligue) = SeasonDoubleRanking(Data.currentSeason, ligue.tournaments, Nil)

  def team(ligue: Ligue) = SeasonTeamRanking(Data.currentSeason, Nil)

}


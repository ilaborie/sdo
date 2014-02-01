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
import play.api.i18n.Messages

sealed abstract class RankingType {
  def canParticipate(player: Participant): Boolean
}

case class Single(comite: PlayerContainer) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case _: NotLicensedPlayer => true
    case lp: LicensedPlayer => comite.players.contains(lp)
    case _ => false
  }

  override val toString = Messages("menu.rank.simple")
}

case class SingleLicensied(orga: PlayerContainer) extends RankingType {
  def canParticipate(player: Participant): Boolean = orga.isMember(player)

  override val toString = Messages("menu.rank.simple")
}

case class Mens(comite: PlayerContainer) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case p: NotLicensedPlayer => !p.lady
    case p: LicensedPlayer => !p.lady && comite.players.contains(p)
    case _ => false
  }
  override val toString = Messages("menu.rank.men")
}

case class MensLicensied(orga: PlayerContainer) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case p: Player => !p.lady && orga.isMember(player)
    case _ => false
  }
  override val toString = Messages("menu.rank.men")
}

case class Ladies(comite: PlayerContainer) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case p: NotLicensedPlayer => p.lady
    case p: LicensedPlayer => p.lady && comite.players.contains(p)
    case _ => false
  }
  override val toString = Messages("menu.rank.women")
}

case class LadiesLicensied(orga: PlayerContainer) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case p: Player => p.lady && orga.isMember(player)
    case _ => false
  }
  override val toString = Messages("menu.rank.women")
}

case class Youth(comite: PlayerContainer) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case p: NotLicensedPlayer => p.youth
    case p: LicensedPlayer => p.youth && comite.players.contains(p)
    case _ => false
  }
  override val toString = Messages("menu.rank.junior")
}

case class YouthLicensied(orga: PlayerContainer) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case p: Player => p.youth && orga.isMember(player)
    case _ => false
  }
  override val toString = Messages("menu.rank.junior")
}

case class Pairs(comite: PlayerContainer) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case p: Pair => comite.isMember(p)
    case _ => false
  }
  override val toString = Messages("menu.rank.double")
}

case class PairsLicensied(orga: PlayerContainer) extends RankingType {
  def canParticipate(player: Participant): Boolean = player match {
    case p: Pair => orga.isMember(player)
    case _ => false
  }
  override val toString = Messages("menu.rank.double")
}


object ComiteRanking {

  val season = Season.currentSeason

  def single(comite: Comite) = SeasonSingleRanking(season, comite, (pos: Int) => pos < 7)

  def ladies(comite: Comite) = SeasonLadiesRanking(season, comite, (pos: Int) => pos < 4)

  def youth(comite: Comite) = SeasonYouthRanking(season, comite, (pos: Int) => pos < 4)

  def pairs(comite: Comite) = SeasonPairsRanking(season, comite, (pos: Int) => pos < 4)

  def team(comite: Comite) = SeasonTeamRanking(season, comite)

}

object InterComiteRanking {

  val season = Season.currentSeason

  def getPoints(position: Int) = position match {
    case 1 => 22
    case 2 => 18
    case 3 => 15
    case 4 => 13
    case 5 => 12
    case 6 => 11
    case 7 => 10
    case 8 => 9
    case 9 => 8
    case 10 => 7
    case 11 => 6
    case 12 => 5
    case 13 => 4
    case 14 => 3
    case 15 => 2
    case 16 => 1
    case _ => 0
  }

  private def getInterComiteTournaments(ligue: Ligue) = {
    for {
      comite <- ligue.comites
      tournament <- comite.tournaments
    } yield tournament
  }.toList.sortBy(_.date)

  def mens(ligue: Ligue) =
    SeasonSingleRanking(season, MensLicensied(ligue), ligue.players.toList, getInterComiteTournaments(ligue), _ => false)

  def ladies(ligue: Ligue) =
    SeasonLadiesRanking(season, LadiesLicensied(ligue), ligue.players.toList, getInterComiteTournaments(ligue), _ => false)

  def youth(ligue: Ligue) =
    SeasonYouthRanking(season, YouthLicensied(ligue), ligue.players.toList, getInterComiteTournaments(ligue), _ => false)

  def pairs(ligue: Ligue) = {
    val tournaments = getInterComiteTournaments(ligue)
    val pairs = {
      for {
        tour <- tournaments
        pair <- tour.getPairs
      } yield pair
    }.toSet.toList
    SeasonPairsRanking(season, PairsLicensied(ligue), pairs, tournaments, _ => false)
  }

  def team(ligue: Ligue) = SeasonTeamRanking(season, ligue)

}

object LigueRanking {

  val season = Season.currentSeason

  def mens(ligue: Ligue) = SeasonSingleRanking(season, ligue, (pos: Int) => pos < 5)

  def ladies(ligue: Ligue) = SeasonLadiesRanking(season, ligue, (pos: Int) => pos < 3)

  def youth(ligue: Ligue) = SeasonYouthRanking(season, ligue, (pos: Int) => pos < 3)

  def pairs(ligue: Ligue) = SeasonPairsRanking(season, ligue, (pos: Int) => pos < 3)

}


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

package model.orga

import play.api.i18n.Messages

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat

import util.Location
import model.rank._

/**
 * Tournament
 */
sealed trait Tournament {
  def date: LocalDate

  def shortName: String

  def place: Option[Location]

  def ligue: Ligue

  def maybyComite: Option[Comite]

  def info: Option[Info] = None

  def getPoint(position: TournamentResult, rankingType: RankingType): Int

  def getMappingRoundRobin(rankingType: RankingType): List[TournamentResult]

  def getPointAsString(position: TournamentResult, rankingType: RankingType): String = {
    val point = getPoint(position, rankingType)
    if (point == 0) "-" else point.toString
  }

  def getPairs: Seq[Pair]

  // higher is better
  def getPriority: Int
}

object Tournament {

  val orderByDate: Ordering[Tournament] = Ordering.by[Tournament, LocalDate](_.date)
}

/**
 * Base tournament
 * @param file the result file
 */
sealed abstract class BaseTournament(val file: String) extends Tournament {
  def isSimple: Boolean

  private lazy val data = TournamentResultData.readBaseTournamentResult(file)

  lazy val mens: Option[TournamentResults[Player]] = data._1
  lazy val ladies: Option[TournamentResults[Player]] = data._2
  lazy val youth: Option[TournamentResults[Player]] = data._3
  lazy val pairs: Option[TournamentResults[Pair]] = data._4

  lazy val isPlayed = mens.isDefined || ladies.isDefined || youth.isDefined || pairs.isDefined

  lazy val getPairs: Seq[Pair] = pairs match {
    case None => Nil
    case Some(res) => res.allParticipants
  }

  private def mappingRoundRobin[T <: Participant](oResult: Option[TournamentResults[T]]): List[TournamentResult] = oResult match {
    case Some(res) => {
      val list = List(Winner, RunnerUp, SemiFinal, QuarterFinal, EighthFinal, SixteenthFinal, ThirtySecondFinal, SixtyForthFinal)
      if (res.winner.isEmpty) Nil
      else if (res.runnerUp.isEmpty) list.drop(1)
      else if (res.semiFinal.isEmpty) list.drop(2)
      else if (res.quarterFinal.isEmpty) list.drop(3)
      else if (res.eighthFinal.isEmpty) list.drop(4)
      else if (res.sixteenthFinal.isEmpty) list.drop(5)
      else if (res.thirtySecondFinal.isEmpty) list.drop(6)
      else Nil
    }
    case None => Nil
  }

  def getMappingRoundRobin(rankingType: RankingType) = rankingType match {
    case _: Single => mappingRoundRobin(mens)
    case _: SingleLicensied => mappingRoundRobin(mens)
    case _: Mens => mappingRoundRobin(mens)
    case _: MensLicensied => mappingRoundRobin(mens)
    case _: Ladies => mappingRoundRobin(ladies)
    case _: LadiesLicensied => mappingRoundRobin(ladies)
    case _: Youth => mappingRoundRobin(youth)
    case _: YouthLicensied => mappingRoundRobin(youth)
    case _: Pairs => mappingRoundRobin(pairs)
    case _: PairsLicensied => mappingRoundRobin(pairs)
  }
}

/**
 * Ligue Tournament
 */
sealed trait LigueTournament extends Tournament {
  val isEvent: Boolean = true
  val isTeam: Boolean = false
  val maybyComite = None
}

/**
 * Coupe Ligue
 */
case class CoupeLigue(date: LocalDate, location: Location, override val file: String, override val info: Option[Info] = None) extends BaseTournament(file) with LigueTournament {

  override def toString = Messages("rank.ligue.coupe.title", ligue.name)

  override val isSimple = false

  val place = Some(location)

  val shortName = "CL"

  lazy val ligue = Ligue.ligues.find(_.coupe == this).get

  def getPoint(position: TournamentResult, rankingType: RankingType): Int = position match {
    case Winner => 22
    case RunnerUp => 16
    case SemiFinal => 11
    case QuarterFinal => 7
    case EighthFinal => 4
    case SixteenthFinal => 2
    case ThirtySecondFinal => 1
    case RoundRobin(pos) => if (pos > 2) getPoint(getMappingRoundRobin(rankingType)(pos - 3), rankingType) else 1
    case NoParticipation => 0
    case _ => 1
  }

  val getPriority = 1

}

/**
 * Open Ligue
 */
case class OpenLigue(date: LocalDate, location: Location, override val file: String, override val info: Option[Info] = None) extends BaseTournament(file) with LigueTournament {

  override def toString = Messages("rank.ligue.open.title", ligue.name)

  override val isSimple = true

  val place = Some(location)

  lazy val ligue = Ligue.ligues.find(_.opens.contains(this)).get

  val shortName = s"OL-${DateTimeFormat.forPattern("yyyyMMdd").print(date)}"

  def getPoint(position: TournamentResult, rankingType: RankingType): Int = position match {
    case Winner => 16
    case RunnerUp => 11
    case SemiFinal => 7
    case QuarterFinal => 4
    case EighthFinal => 2
    case SixteenthFinal => 1
    case RoundRobin(pos) => if (pos > 2) getPoint(getMappingRoundRobin(rankingType)(pos - 3), rankingType) else 1
    case NoParticipation => 0
    case _ => 1 // Participation
  }

  val getPriority = 2
}

/**
 * Master Ligue
 */
case class MasterLigue(date: LocalDate, location: Location, override val file: String, override val info: Option[Info] = None) extends BaseTournament(file) with LigueTournament {

  override def toString = Messages("rank.ligue.master.title")

  override val isSimple = false

  val place = Some(location)

  val shortName = "Mast"

  lazy val ligue = Ligue.ligues.find(_.master == this).get

  def getPoint(position: TournamentResult, rankingType: RankingType): Int = position match {
    case Winner => 29
    case RunnerUp => 22
    case SemiFinal => 16
    case QuarterFinal => 11
    case EighthFinal => 7
    case RoundRobin(pos) => pos match {
      case 3 => 4
      case 4 => 2
      case _ => 0
    }
    case _ => 0
  }

  val getPriority = 0
}

/**
 * Master Ligue Team
 */
case class MasterLigueTeam(date: LocalDate, location: Location, override val info: Option[Info] = None) extends LigueTournament {
  override val isTeam: Boolean = true
  val shortName = "MastTeam"
  lazy val ligue = Ligue.ligues.find(_.masterTeam == this).get
  val place = Some(location)

  override def toString = Messages("rank.ligue.master.team.title")

  def getPoint(position: TournamentResult, rankingType: RankingType): Int = 0

  lazy val getPairs: Seq[Pair] = Nil
  val getPriority = 0

  def getMappingRoundRobin(rankingType: RankingType): List[TournamentResult] = Nil
}

/**
 * Coupe Ligue Team
 */
case class CoupeLigueTeam(date: LocalDate, location: Location, override val info: Option[Info] = None) extends LigueTournament {
  override val isTeam: Boolean = true
  val shortName = "CLTeam"
  lazy val ligue = Ligue.ligues.find(_.coupeTeam == Some(this)).get
  val place = Some(location)

  override def toString = Messages("rank.ligue.coupe.team.title")

  def getPoint(position: TournamentResult, rankingType: RankingType): Int = 0
  def getMappingRoundRobin(rankingType: RankingType): List[TournamentResult] = Nil

  lazy val getPairs: Seq[Pair] = Nil
  val getPriority = 0
}

/**
 * Inter-Comite Ranking
 */
case class ComiteRank(date: LocalDate) extends LigueTournament {

  override def toString = Messages("rank.ligue.comite.rank.title")

  val place = None

  lazy val ligue = Ligue.ligues.find(_.tournaments.contains(this)).get

  val shortName = "IC"

  override val isEvent: Boolean = false

  def getPoint(position: TournamentResult, rankingType: RankingType): Int = position match {
    case RoundRobin(pos) => InterComiteRanking.getPoints(pos)
    case _ => 0
  }
  def getMappingRoundRobin(rankingType: RankingType): List[TournamentResult] = Nil

  lazy val getPairs: Seq[Pair] = {
    val results: Seq[TournamentResults[Pair]] = for {
      comite <- ligue.comites
      tournaments <- comite.tournaments
      result <- tournaments.pairs
    } yield result

    val pairs: Seq[Pair] = for {
      res <- results
      participant <- res.allParticipants
    } yield participant

    pairs.toSet.toSeq
  }

  val getPriority = 3
}

/**
 * National Tournament
 * @param shortName shortName
 * @param date date
 */
case class NationalTournament(shortName: String,
                              date: LocalDate,
                              mensInfo: Map[Int, List[String]],
                              ladiesInfo: Map[Int, List[String]],
                              youthInfo: Map[Int, List[String]],
                              pairsInfo: Map[Int, List[(String, String)]]) extends LigueTournament {

  lazy val mens: Map[Participant, Int] = {
    for {
      (m, lst) <- mensInfo
      s <- lst
      p <- LicensedPlayer.findByName(s)
    } yield (p, m)
  }.toMap
  lazy val ladies: Map[Participant, Int] = {
    for {
      (m, lst) <- ladiesInfo
      s <- lst
      p <- LicensedPlayer.findByName(s)
    } yield (p, m)
  }.toMap
  lazy val youth: Map[Participant, Int] = {
    for {
      (m, lst) <- youthInfo
      s <- lst
      p <- LicensedPlayer.findByName(s)
    } yield (p, m)
  }.toMap
  lazy val pairs: Map[Participant, Int] = {
    for {
      (m, lst) <- pairsInfo
      (s1, s2) <- lst
      p1 <- LicensedPlayer.findByName(s1)
      p2 <- LicensedPlayer.findByName(s2)
    } yield (Pair(p1, p2), m)
  }.toMap

  override def toString = Messages(s"rank.ligue.national.${shortName.toLowerCase}.title")

  val place = None

  lazy val ligue: Ligue = Ligue.ligues.find(_.nationalTournaments.contains(this)).get

  override val isEvent: Boolean = false

  def getPoint(position: TournamentResult, rankingType: RankingType): Int = position match {
    case WinningMatch(win) => 1 + win
    case _ => 0
  }
  def getMappingRoundRobin(rankingType: RankingType): List[TournamentResult] = Nil

  lazy val getPairs: Seq[Pair] = pairs.keySet.map(_.asInstanceOf[Pair]).toSeq

  val getPriority = 4
}

object NationalTournament {
  val tournamentList = List("open-france", "coupe-france", "open-national", "open-fede")
}

/**
 * Comite Tournament
 */
sealed trait ComiteTournament extends BaseTournament {
  def shortName: String

  def comite: Comite

  lazy val ligue = comite.ligue
  lazy val maybyComite = Some(comite)
}

/**
 * Coupe Comite
 */
case class CoupeComite(date: LocalDate, location: Location, override val file: String, override val info: Option[Info] = None) extends BaseTournament(file) with ComiteTournament {
  override def toString = Messages("rank.comite.coupe.title", comite.name)

  val shortName = "CC"
  val place = Some(location)

  val isSimple = false

  lazy val comite: Comite = Ligue.comites.find(_.coupe == this).get

  def getPoint(position: TournamentResult, rankingType: RankingType): Int = position match {
    case Winner => 29
    case RunnerUp => 22
    case SemiFinal => 16
    case QuarterFinal => 11
    case EighthFinal => 7
    case SixteenthFinal => 4
    case ThirtySecondFinal => 2
    case SixtyForthFinal => 1
    case RoundRobin(pos) => if (pos > 2) getPoint(getMappingRoundRobin(rankingType)(pos - 3), rankingType) else 1
    case NoParticipation => 0
    case _ => 1 // Participation
  }

  val getPriority = 0
}

/**
 * Open Club
 */
case class OpenClub(date: LocalDate, location: Location, override val file: String, override val info: Option[Info] = None) extends BaseTournament(file) with ComiteTournament {
  override def toString = Messages("rank.comite.open.title", club.name)

  val isSimple = true

  val place = Some(location)
  val shortName = s"OC-${DateTimeFormat.forPattern("yyyyMMdd").print(date)}"

  lazy val club: Club = Ligue.clubs.find(_.opens.contains(this)).get
  lazy val comite = club.comite

  def getPoint(position: TournamentResult, rankingType: RankingType): Int = position match {
    case Winner => 22
    case RunnerUp => 16
    case SemiFinal => 11
    case QuarterFinal => 7
    case EighthFinal => 4
    case SixteenthFinal => 2
    case ThirtySecondFinal => 1
    case RoundRobin(pos) => if (pos > 2) getPoint(getMappingRoundRobin(rankingType)(pos - 3), rankingType) else 1
    case NoParticipation => 0
    case _ => 1 // Participation
  }

  val getPriority = 1
}





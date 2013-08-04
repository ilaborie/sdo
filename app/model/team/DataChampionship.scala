package model.team

import java.util.{List => JavaList, Map => JavaMap}

import scala.collection.JavaConversions._

import play.api.Logger

import scala.Predef._
import scala.Some

import model.orga._
import util.YamlParser

/**
 * DataChampionship
 */
object DataChampionship {

  private val logger = Logger("data")

  /**
   * Find a Team
   * @param ligue the ligue
   * @param shortName the team short name
   * @return the team
   * @throws ParseDataException if not found
   */
  private def shouldFindTeam(ligue: Ligue, shortName: String): Team = ligue.findTeamByShortName(shortName) match {
    case Some(team) => team
    case None => throw ParseDataException(s"Cannot find team $shortName in $ligue")
  }

  /**
   * Find a Licensied Player
   * @param name the player full name (lastname, firstname)
   * @return the player
   * @throws ParseDataException if not found
   */
  private def shouldFindLicensiedPlayer(name: String): LicensedPlayer = LicensedPlayer.findByName(name) match {
    case Some(player) => player
    case None => throw ParseDataException(s"Cannot find player $name")
  }

  /**
   * Read TeamChampionship
   * @param ligue ligue
   * @return TeamChampionship
   */
  def readChampionship(season: Season, ligue: Ligue): TeamChampionship = {
    val champFile = s"data/s$season/${ligue.shortName}/teamChampionship/championship.yml"
    logger.info(s"Read TeamChampionship information in $champFile")

    val dayList = YamlParser.parseFile(champFile).asInstanceOf[JavaList[JavaMap[String, Any]]]
    logger.trace(s"Read $dayList")

    val champDays = for (day <- dayList.toList) yield readChampionshipDay(season, ligue, day.toMap)

    TeamChampionship(season, ligue, champDays)
  }

  /**
   * TeamChampionshipDay
   * @param ligue ligue
   * @param dayMap day
   * @return the championship day
   */
  private def readChampionshipDay(season: Season, ligue: Ligue, dayMap: Map[Any, Any]): TeamChampionshipDay = {
    val day: Int = dayMap("day").asInstanceOf[Integer]
    val from = Data.readDate(dayMap("from").asInstanceOf[String])
    val to = Data.readDate(dayMap("to").asInstanceOf[String])
    val matchList = dayMap("matches").asInstanceOf[JavaList[JavaMap[String, String]]]
    logger.trace(s"Read $matchList")
    val matches = for (m <- matchList.toList) yield readPlannedTeamMatch(season, ligue, day, m.toMap)

    TeamChampionshipDay(ligue, day, from, to, matches)
  }

  /**
   * PlannedTeamMatch
   * @param ligue ligue
   * @param day day
   * @param m match
   * @return PlannedTeamMatch
   */
  private def readPlannedTeamMatch(season: Season, ligue: Ligue, day: Int, m: Map[String, String]): PlannedTeamMatch = {
    val team1 = shouldFindTeam(ligue, m("team1"))
    val team2 = shouldFindTeam(ligue, m("team2"))

    PlannedTeamMatch(day, team1, team2, readDetail(season, ligue, day, team1, team2))
  }

  /**
   * Read detail
   * @param ligue ligue
   * @param day day
   * @param team1 team1
   * @param team2 team2
   * @return Detail
   */
  def readDetail(season: Season, ligue: Ligue, day: Int, team1: Team, team2: Team): Option[MatchDetail] = {
    val detailFile = s"data/s$season/${ligue.shortName}/teamChampionship/d$day/${team1.shortName}-${team2.shortName}.yml"
    logger.info(s"Read TeamChampionship information in $detailFile")

    val detailMap = YamlParser.tryParseFile(detailFile)
    detailMap.map(
      detailM => {
        val detail: JavaMap[String, Any] = detailM.asInstanceOf[JavaMap[String, Any]]
        logger.trace(s"Read $detail")

        val date = Data.readDate(detail.get("date").asInstanceOf[String])
        val location = detail.get("location").asInstanceOf[String]

        val t1: TeamMatchDetail = readTeamMatchDetail(team1, detail.get("team1")
          .asInstanceOf[JavaMap[String, Any]].toMap)
        val t2: TeamMatchDetail = readTeamMatchDetail(team2, detail.get("team2")
          .asInstanceOf[JavaMap[String, Any]].toMap)

        val matchesList = detail.get("matches")
        logger.trace(s"Read matches: $matchesList")
        val matches: List[Match] = readMatchs(t1, t2, matchesList.asInstanceOf[JavaMap[String, Any]].toMap)

        PlayedMatchDetail(day, date, location, t1, t2, matches)
      })
  }

  /**
   * Read TeamMatchDetail
   * @param team the team
   * @param map data
   * @return the TeamMatchDetail
   */
  def readTeamMatchDetail(team: Team, map: Map[String, Any]): TeamMatchDetail = {
    val playersName = map("joueurs").asInstanceOf[JavaList[String]].toList
    logger.debug(s"Read players from $playersName")

    val capitain = shouldFindLicensiedPlayer(map("capitain").asInstanceOf[String])
    val players = playersName map shouldFindLicensiedPlayer
    val substitute: Option[Substitute] = readSubstitute(map("substitute").asInstanceOf[JavaMap[String, Any]])
    val doublettes: (TeamDoublette, TeamDoublette) = readDoublettes(map("doubles")
      .asInstanceOf[JavaList[JavaMap[String, String]]].toList)

    TeamMatchDetail(team, capitain, players.toArray, substitute, doublettes)
  }

  /**
   * Read Substitute
   * @param map data
   * @return the Substitute
   */
  def readSubstitute(map: JavaMap[String, Any]): Option[Substitute] = {
    if (map != null) {
      val m = map.toMap
      logger.debug(s"Read substitute from $m")

      val j = m("j").asInstanceOf[String]
      val inPlayer = if (j != null) LicensedPlayer.findByName(j) else None

      val out = m("out").asInstanceOf[String]
      val outPlayer = if (out != null) LicensedPlayer.findByName(out) else None

      val after = m("match").asInstanceOf[Integer]
      val afterMatch = if (after != null) Some(after.toInt) else None

      if (inPlayer.isDefined) Some(Substitute(inPlayer.get, outPlayer, afterMatch)) else None
    } else None
  }

  /**
   * Read Doublettes
   * @param list data
   * @return Doublettes
   */
  def readDoublettes(list: List[JavaMap[String, String]]): (TeamDoublette, TeamDoublette) = {
    (readDoublette(list(0).toMap), readDoublette(list(1).toMap))
  }

  /**
   * Read Doublette
   * @param map data
   * @return the Doublette
   */
  def readDoublette(map: Map[String, String]): TeamDoublette =
    TeamDoublette(shouldFindLicensiedPlayer(map("j1")), shouldFindLicensiedPlayer(map("j2")))


  /**
   * List Match
   * @param detail1 team1 detail
   * @param detail2 team2 detail
   * @param data data
   * @return All matchs
   */
  def readMatchs(detail1: TeamMatchDetail, detail2: TeamMatchDetail, data: Map[String, Any]): List[Match] = {
    def getMatchData(i: Int): Map[String, Int] = data(s"match$i").asInstanceOf[JavaMap[String, Int]].toMap

    val res = for {
      i <- 1 to 20
      player1 <- Some(detail1.getPlayer1(i))
      player2 <- Some(detail2.getPlayer2(i))
    } yield Match(
        detail1.team,
        detail2.team,
        player1,
        player2,
        TeamMatchDetail.isPlayer1Start(i),
        readLegs(player1, player2, getMatchData(i)))

    res.toList
  }

  /**
   * Read Legs
   * @param player1 player 1
   * @param player2 player 2
   * @param data data
   * @return legs
   */
  def readLegs(player1: TeamParticipant, player2: TeamParticipant, data: Map[String, Any]): (Leg, Leg, Option[Leg]) = {
    def int2Leg(i: Int) = i match {
      case 1 => Leg(player1)
      case 2 => Leg(player2)
      case _ => throw ParseDataException(s"Unexpected leg value: $i (expected 1 or 2)")
    }

    val leg1 = int2Leg(data("l1").asInstanceOf[Int])
    val leg2 = int2Leg(data("l2").asInstanceOf[Int])

    val l3 = data("l3").asInstanceOf[Integer]
    val leg3 = if (l3 != null) Some(int2Leg(l3.toInt)) else None

    (leg1, leg2, leg3)
  }

}

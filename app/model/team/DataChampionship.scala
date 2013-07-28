package model.team

import java.util.{List => JavaList, Map => JavaMap}

import scala.collection.JavaConversions._

import play.api.Logger
import play.libs.Yaml

import model.orga.{Data, Ligue}


/**
 * DataChampionship
 */
object DataChampionship {

  private val logger = Logger("data")
  private val currentSeason = Data.currentSeason

  /**
   * Read Championship
   * @param ligue ligue
   * @return Championship
   */
  def readChampionship(ligue: Ligue) = {
    val champFile = s"data/s$currentSeason/${ligue.shortName}/teamChampionship/championship.yml"
    logger.info(s"Read Championship information in $champFile")

    val dayList = Yaml.load(champFile).asInstanceOf[JavaList[JavaMap[String, Any]]]
    logger.trace(s"Read $dayList")

    val champDays = for (day <- dayList.toList) yield readChampionshipDay(ligue, day.toMap)

    Championship(currentSeason, champDays)
  }

  /**
   * ChampionshipDay
   * @param ligue ligue
   * @param dayMap day
   * @return the championship day
   */
  private def readChampionshipDay(ligue: Ligue, dayMap: Map[Any, Any]): ChampionshipDay = {
    val day: Int = dayMap("day").asInstanceOf[Integer]
    val matchList = dayMap("matches").asInstanceOf[JavaList[JavaMap[String, String]]]
    logger.trace(s"Read $matchList")
    val matches = for (m <- matchList.toList) yield readPlannedTeamMatch(ligue, day, m.toMap)

    ChampionshipDay(day, matches)
  }

  /**
   * PlannedTeamMatch
   * @param ligue ligue
   * @param day day
   * @param m match
   * @return PlannedTeamMatch
   */
  private def readPlannedTeamMatch(ligue: Ligue, day: Int, m: Map[String, String]): PlannedTeamMatch = {
    val team1 = ligue.findTeamByName(m("team1"))
    if (team1.isEmpty) throw new RuntimeException(s"Oops: nom d'équipe inconnue: ${m("team1")}")

    val team2 = ligue.findTeamByName(m("team2"))
    if (team2.isEmpty) throw new RuntimeException(s"Oops: nom d'équipe inconnue: ${m("team2")}")

    PlannedTeamMatch(day, team1.get, team2.get)
  }


}

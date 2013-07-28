package model.orga

import play.libs.Yaml
import java.util.{List => JavaList}
import java.util.{Map => JavaMap}

import scala.collection.JavaConversions._
import play.api.Logger
import play.Play
import com.google.common.io.{ByteStreams, CharStreams}
import com.google.common.base.Charsets

/**
 * User: igorlaborie
 * Date: 27/07/13
 * Time: 17:51
 */
object Data {
  private val logger = Logger("data")


  val seasons: List[Season] = List("2013-2014")
  val currentSeason: Season = seasons.last

  /**
   * Read list of ligue
   * @return all ligues
   */
  def readLigues() = {
    val liguesFile = s"data/s$currentSeason/ligues.yml"
    logger.info(s"Read ligues information in $liguesFile")

    val liguesList = Yaml.load(liguesFile).asInstanceOf[JavaList[String]]
    logger.trace(s"Read $liguesList")

    for (ligue <- liguesList.toList) yield readLigue(ligue)
  }

  /**
   * Read a ligue
   * @param ligue ligue perfix
   * @return a ligue
   */
  def readLigue(ligue: String): Ligue = {
    val ligueFile = s"data/s$currentSeason/$ligue/ligue.yml"
    logger.info(s"Read ligue information in $ligueFile")
    val info = Yaml.load(ligueFile).asInstanceOf[JavaMap[String, AnyRef]].toMap
    logger.trace(s"Read $info")

    val name = info("name").asInstanceOf[String]
    val shortName = info("shortname").asInstanceOf[String]
    val comitesList = info("comites").asInstanceOf[JavaList[String]].toList
    val comites = for (comite <- comitesList) yield readComite(ligue, comite)
    val infomation = readInfo(s"data/s$currentSeason/$ligue/info.html")

    Ligue(name, shortName, comites, infomation)
  }


  /**
   * Read a comite
   * @param ligue the ligue prefix
   * @param comite the comite prefix
   * @return a comite
   */
  def readComite(ligue: String, comite: String): Comite = {
    val comiteFile = s"data/s$currentSeason/$ligue/$comite/comite.yml"
    logger.info(s"Read comite information in $comiteFile")
    val info = Yaml.load(comiteFile).asInstanceOf[JavaMap[String, AnyRef]].toMap
    logger.trace(s"Read $info")

    val name = info("name").asInstanceOf[String]
    val shortName = info("shortname").asInstanceOf[String]
    val clubList = info("clubs").asInstanceOf[JavaList[String]].toList
    val clubs = for (club <- clubList) yield readClub(ligue, comite, club)
    val infomation = readInfo(s"data/s$currentSeason/$ligue/$comite/info.html")
    Comite(name, shortName, clubs, infomation)
  }

  /**
   * Read Club
   * @param ligue the ligue
   * @param comite the comite
   * @param club the club
   * @return a Club
   */
  def readClub(ligue: String, comite: String, club: String): Club = {
    val clubFile = s"data/s$currentSeason/$ligue/$comite/$club/club.yml"
    logger.info(s"Read club information in $clubFile")
    val info = Yaml.load(clubFile).asInstanceOf[JavaMap[String, AnyRef]].toMap
    logger.trace(s"Read $info")

    val name = info("name").asInstanceOf[String]
    val shortName = info("shortname").asInstanceOf[String]
    val teamList = info("teams").asInstanceOf[JavaList[String]].toList
    val teams = for (team <- teamList) yield readTeam(ligue, comite, club, team)
    val infomation = readInfo(s"data/s$currentSeason/$ligue/$comite/$club/info.html")
    Club(name, shortName, teams, infomation)
  }

  /**
   * Read team
   * @param ligue ligue
   * @param comite comite
   * @param club club
   * @param team team
   * @return the team
   */
  def readTeam(ligue: String, comite: String, club: String, team: String): Team = {
    val teamFile = s"data/s$currentSeason/$ligue/$comite/$club/$team.yml"
    logger.info(s"Read team information in $teamFile")
    val info = Yaml.load(teamFile).asInstanceOf[JavaMap[String, AnyRef]].toMap
    logger.trace(s"Read $info")

    val name = info("name").asInstanceOf[String]
    val playerList = info("players").asInstanceOf[JavaList[JavaMap[String, String]]].toList
    val players = for (player <- playerList) yield createLicensedPlayer(player.toMap)
    Team(name, players)
  }

  /**
   * Create LicensedPlayer
   * @param data data
   * @return player
   */
  def createLicensedPlayer(data: Map[String, String]): LicensedPlayer = {
    val license = data.getOrElse("license", "???")
    val firstName = data.getOrElse("firstname", "???")
    val lastName = data.getOrElse("lastname", "???")
    val surname = if (data.contains("surename")) data.get("surname") else data.get("commonname")
    val isFeminine = data.contains("feminine")
    val isJunior = data.contains("junior")

    LicensedPlayer(license, s"$firstName $lastName", surname, feminine = isFeminine, junior = isJunior)
  }


  /**
   * Read info file
   * @param infoFile info file
   * @return the info
   */
  def readInfo(infoFile: String): Option[Info] = {
    val stream = Play.application.resourceAsStream(infoFile)
    if (stream != null) {
      val supplier = ByteStreams.newInputStreamSupplier(ByteStreams.toByteArray(stream))
      val input = CharStreams.newReaderSupplier(supplier, Charsets.UTF_8)
      val info = CharStreams.toString(input)
      Some(info)
    } else None
  }
}

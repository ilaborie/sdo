package model.orga

import java.util.{List => JavaList, Map => JavaMap, Calendar}
import java.text.SimpleDateFormat

import scala.collection.JavaConversions._

import play.Play
import play.api.Logger

import com.google.common.io.{ByteStreams, CharStreams}
import com.google.common.base.Charsets
import util.YamlParser

/**
 * Data helpers
 */
object Data {

  private val logger = Logger("data")
  private val dateFormater = new SimpleDateFormat("dd-MM-yyyy")

  def readDate(date: String): Calendar = {
    val cal = Calendar.getInstance()
    cal.setTime(dateFormater.parse(date))
    cal
  }

  /**
   * Read all not licensied players
   * @return players
   */
  def readNotLicensedPlayers(season: Season): List[NotLicensedPlayer] = {
    val nlFile = s"data/s$season/nl.yml"
    logger.info(s"Read not licensied players in $nlFile")

    val nlList = YamlParser.parseFile(nlFile).asInstanceOf[JavaList[JavaMap[String, String]]]
    logger.trace(s"Read $nlList")

    for (nl <- nlList.toList) yield readNotLicensiedPlayer(nl.toMap)
  }

  /**
   * Read not licensed player
   * @param data data
   * @return the player
   */
  def readNotLicensiedPlayer(data: Map[String, String]): NotLicensedPlayer = {
    val firstName = data.getOrElse("firstname", "???")
    val lastName = data.getOrElse("lastname", "???")
    val isFeminine = data.contains("feminine")
    val isJunior = data.contains("junior")

    NotLicensedPlayer(s"$lastName $firstName", feminine = isFeminine, junior = isJunior)
  }

  /**
   * Read list of ligue
   * @return all ligues
   */
  def readLigues(season: Season) = {
    val liguesFile = s"data/s$season/ligues.yml"
    logger.info(s"Read ligues information in $liguesFile")

    val liguesList = YamlParser.parseFile(liguesFile).asInstanceOf[JavaList[String]]
    logger.trace(s"Read $liguesList")

    for (ligue <- liguesList.toList) yield readLigue(season, ligue)
  }

  /**
   * Read a ligue
   * @param ligue ligue perfix
   * @return a ligue
   */
  def readLigue(season: Season, ligue: String): Ligue = {
    val ligueFile = s"data/s$season/$ligue/ligue.yml"
    logger.info(s"Read ligue information in $ligueFile")
    val info = YamlParser.parseFile(ligueFile).asInstanceOf[JavaMap[String, AnyRef]].toMap
    logger.trace(s"Read $info")

    val name = info("name").asInstanceOf[String]
    val shortName = info("shortname").asInstanceOf[String]

    val comitesList = info("comites").asInstanceOf[JavaList[String]].toList
    val comites = for (comite <- comitesList) yield readComite(season, ligue, comite)

    val openList = info("opens").asInstanceOf[JavaList[JavaMap[String,String]]].toList
    val opens = for (open <- openList) yield OpenLigue(readDate(open.get("date")), open.get("location"))

    val coupeMap = info("coupe").asInstanceOf[JavaMap[String,String]]
    val coupe = CoupeLigue(readDate(coupeMap.get("date")), coupeMap.get("location"))

    val masterMap = info("master").asInstanceOf[JavaMap[String,String]]
    val master = MasterLigue(readDate(masterMap.get("date")), masterMap.get("location"))

    val teamMaster = info("team-master").asInstanceOf[JavaMap[String,String]]
    val masterTeam = MasterLigueTeam(readDate(teamMaster.get("date")), teamMaster.get("location"))

    val information = readInfo(s"data/s$season/$ligue/ligue.html")

    Ligue(name, shortName, comites, opens, coupe, master, masterTeam, information)
  }


  /**
   * Read a comite
   * @param ligue the ligue prefix
   * @param comite the comite prefix
   * @return a comite
   */
  def readComite(season: Season, ligue: String, comite: String): Comite = {
    val comiteFile = s"data/s$season/$ligue/$comite/comite.yml"
    logger.info(s"Read comite information in $comiteFile")
    val info = YamlParser.parseFile(comiteFile).asInstanceOf[JavaMap[String, AnyRef]].toMap
    logger.trace(s"Read $info")

    val name = info("name").asInstanceOf[String]
    val shortName = info("shortname").asInstanceOf[String]
    val clubList = info("clubs").asInstanceOf[JavaList[String]].toList

    val clubs = for (club <- clubList) yield readClub(season, ligue, comite, club)
    val coupeMap = info("coupe").asInstanceOf[JavaMap[String,String]]

    val coupe = CoupeComite(readDate(coupeMap.get("date")), coupeMap.get("location"))
    val information = readInfo(s"data/s$season/$ligue/$comite/ligue.html")

    Comite(name, shortName, clubs, coupe, information)
  }

  /**
   * Read Club
   * @param ligue the ligue
   * @param comite the comite
   * @param club the club
   * @return a Club
   */
  def readClub(season: Season, ligue: String, comite: String, club: String): Club = {
    val clubFile = s"data/s$season/$ligue/$comite/$club/club.yml"
    logger.info(s"Read club information in $clubFile")
    val info = YamlParser.parseFile(clubFile).asInstanceOf[JavaMap[String, AnyRef]].toMap
    logger.trace(s"Read $info")

    val name = info("name").asInstanceOf[String]
    val shortName = info("shortname").asInstanceOf[String]
    val opens = if (info.contains("opens")) {
      val openList = info("opens").asInstanceOf[JavaList[String]].toList
     for (open <- openList) yield OpenClub(readDate(open))
    } else Nil
    val teamList = info("teams").asInstanceOf[JavaList[String]].toList
    val teams = for (team <- teamList) yield readTeam(season, ligue, comite, club, team)
    val information = readInfo(s"data/s$season/$ligue/$comite/$club/ligue.html")

    Club(name, shortName, opens, teams, information)
  }

  /**
   * Read team
   * @param ligue ligue
   * @param comite comite
   * @param club club
   * @param team team
   * @return the team
   */
  def readTeam(season: Season, ligue: String, comite: String, club: String, team: String): Team = {
    val teamFile = s"data/s$season/$ligue/$comite/$club/$team.yml"
    logger.debug(s"Read team information in $teamFile")
    val info = YamlParser.parseFile(teamFile).asInstanceOf[JavaMap[String, AnyRef]].toMap
    logger.trace(s"Read $info")

    val name = info("name").asInstanceOf[String]
    val shortname = info("shortname").asInstanceOf[String]
    val playerList = info("players").asInstanceOf[JavaList[JavaMap[String, String]]].toList
    val players = for (player <- playerList) yield createLicensedPlayer(player.toMap)
    val omit = info.contains("omit")

    Team(name, shortname, players, omit)
  }

  /**
   * Create LicensedPlayer
   * @param data data
   * @return player
   */
  def createLicensedPlayer(data: Map[String, String]): LicensedPlayer = {
    val license = data.getOrElse("license", "license ###")
    val firstName = data.getOrElse("firstname", "???")
    val lastName = data.getOrElse("lastname", "???")
    val surname = if (data.contains("surename")) data.get("surname") else data.get("commonname")
    val isFeminine = data.contains("feminine")
    val isJunior = data.contains("junior")

    LicensedPlayer(license, s"$lastName $firstName", surname, feminine = isFeminine, junior = isJunior)
  }


  /**
   * Read ligue file
   * @param infoFile ligue file
   * @return the ligue
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

case class ParseDataException(message: String) extends RuntimeException(message)

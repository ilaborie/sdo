package model.orga

import java.util.{List => JavaList, Map => JavaMap, Calendar}
import java.text.SimpleDateFormat

import scala.collection.JavaConversions._

import play.Play
import play.api.Logger
import play.libs.Yaml

import com.google.common.io.{ByteStreams, CharStreams}
import com.google.common.base.Charsets

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
   * Read all not licensied players
   * @return players
   */
  def readNotLicensedPlayers(): List[NotLicensedPlayer] = {
    val nlFile = s"data/s$currentSeason/nl.yml"
    logger.info(s"Read not licensied players in $nlFile")

    val nlList = Yaml.load(nlFile).asInstanceOf[JavaList[JavaMap[String, String]]]
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
    val openList = info("opens").asInstanceOf[JavaList[String]].toList
    val opens = for (open <- openList) yield OpenLigue(readDate(open))
    val coupe = CoupeLigue(readDate(info("coupe").asInstanceOf[String]))
    val master = MasterLigue(readDate(info("master").asInstanceOf[String]))
    val information = readInfo(s"data/s$currentSeason/$ligue/info.html")

    Ligue(name, shortName, comites, opens, coupe, master, information)
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
    val coupe = CoupeComite(readDate(info("coupe").asInstanceOf[String]))
    val information = readInfo(s"data/s$currentSeason/$ligue/$comite/info.html")

    Comite(name, shortName, clubs, coupe, information)
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
    val openList = info("opens").asInstanceOf[JavaList[String]].toList
    val opens = for (open <- openList) yield OpenClub(readDate(open))
    val teamList = info("teams").asInstanceOf[JavaList[String]].toList
    val teams = for (team <- teamList) yield readTeam(ligue, comite, club, team)
    val information = readInfo(s"data/s$currentSeason/$ligue/$comite/$club/info.html")

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
  def readTeam(ligue: String, comite: String, club: String, team: String): Team = {
    val teamFile = s"data/s$currentSeason/$ligue/$comite/$club/$team.yml"
    logger.debug(s"Read team information in $teamFile")
    val info = Yaml.load(teamFile).asInstanceOf[JavaMap[String, AnyRef]].toMap
    logger.trace(s"Read $info")

    val name = info("name").asInstanceOf[String]
    val playerList = info("players").asInstanceOf[JavaList[JavaMap[String, String]]].toList
    val players = for (player <- playerList) yield createLicensedPlayer(player.toMap)
    val omit = info.contains("omit")

    Team(name, players, omit)
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

    LicensedPlayer(license, s"$lastName $firstName", surname, feminine = isFeminine, junior = isJunior)
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

case class ParseDataException(message: String) extends RuntimeException(message)

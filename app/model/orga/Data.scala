package model.orga

import java.util.{List => JavaList, Map => JavaMap}

import scala.collection.JavaConversions._

import play.api.Logger

import util._

/**
 * Data helpers
 */
object Data {

  private val logger = Logger("data")

  /**
   * Read all not licensied players
   * @return players
   */
  def readNotLicensedPlayers(season: Season): List[NotLicensedPlayer] = {
    val nlFile = s"s$season/nl.yml"
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

    val emails: Set[EMail] = readEmails(data)
    val twitter: Option[String] = data.get("twitter")
    val facebook: Option[String] = data.get("facebook")
    val google: Option[String] = data.get("google")

    NotLicensedPlayer(s"$lastName $firstName", feminine = isFeminine, junior = isJunior,
      emails = emails, twitter = twitter, facebook = facebook, google = google)
  }

  /**
   * Read list of ligue
   * @return all ligues
   */
  def readLigues(season: Season) = {
    val liguesFile = s"s$season/ligues.yml"
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
    val ligueFile = s"s$season/$ligue/ligue.yml"
    logger.info(s"Read ligue information in $ligueFile")
    val info = YamlParser.parseFile(ligueFile).asInstanceOf[JavaMap[String, AnyRef]].toMap
    logger.trace(s"Read $info")

    val name = info("name").asInstanceOf[String]
    val shortName = info("shortname").asInstanceOf[String]

    val comitesList = info("comites").asInstanceOf[JavaList[String]].toList
    val comites = for (comite <- comitesList) yield readComite(season, ligue, comite)

    // Ligue Tournament
    val openList = info("opens").asInstanceOf[JavaList[JavaMap[String, Any]]].toList
    val opens = for (open <- openList) yield OpenLigue(
      YamlParser.readDate(open.get("date").asInstanceOf[String]),
      YamlParser.readLocation(open.toMap).get)

    val coupeMap = info("coupe").asInstanceOf[JavaMap[String, Any]]
    val coupe = CoupeLigue(
      YamlParser.readDate(coupeMap.get("date").asInstanceOf[String]),
      YamlParser.readLocation(coupeMap.toMap).get)

    val masterMap = info("master").asInstanceOf[JavaMap[String, Any]]
    val master = MasterLigue(
      YamlParser.readDate(masterMap.get("date").asInstanceOf[String]),
      YamlParser.readLocation(masterMap.toMap).get)

    val teamMaster = info("team-master").asInstanceOf[JavaMap[String, Any]]
    val masterTeam = MasterLigueTeam(
      YamlParser.readDate(teamMaster.get("date").asInstanceOf[String]),
      YamlParser.readLocation(teamMaster.toMap).get)

    val teamCoupe = {
      if (!info.contains("team-coupe")) None
      else {
        val coupeTeam = info("team-coupe").asInstanceOf[JavaMap[String, Any]]
        Some(CoupeLigueTeam(
          YamlParser.readDate(coupeTeam.get("date").asInstanceOf[String]),
          YamlParser.readLocation(coupeTeam.toMap).get))
      }
    }
    // National Tournament
    val natTournaments = for (tournament <- NationalTournament.tournamentList) yield readNationalTournament(season, ligue, tournament)
    val information = YamlParser.readInfo(s"s$season/$ligue/info.html")

    Ligue(name, shortName, comites, opens, coupe, master, masterTeam, teamCoupe, natTournaments, information)
  }

  /**
   * Read national tournament
   * @param season season
   * @param ligue ligue
   * @param tournament tournament
   * @return tournament
   */
  def readNationalTournament(season: Season, ligue: String, tournament: String): NationalTournament = {
    val tournamentFile = s"s$season/$ligue/championship/$tournament.yml"
    logger.info(s"Read tournament information in $tournamentFile")
    val info: Map[String, Any] = YamlParser.parseFile(tournamentFile).asInstanceOf[JavaMap[String, Any]].toMap
    logger.trace(s"Read $info")

    val shortName = info("shortname").asInstanceOf[String]
    val date = YamlParser.readDate(info("date").asInstanceOf[String])

    def toMapIntStrings(key: String, info: Map[String, Any]): Map[Int, List[String]] = {
      if (info.contains(key)) {
        val value = info(key)
        if (value==null) Map()
        else value.asInstanceOf[JavaMap[Int, JavaList[String]]].toMap.mapValues(_.toList)
      }
      else Map()
    }
    def toMapIntStringsPair(key: String, info: Map[String, Any]): Map[Int, List[(String,String)]] = {
      def lstToPair(lst: JavaList[JavaList[String]]): List[(String, String)] = {
        for (l <- lst.toList) yield (l.get(0), l.get(1))
      }.toList

      if (info.contains(key)) {
        val value = info(key)
        if (value==null) Map()
        else value.asInstanceOf[JavaMap[Int, JavaList[JavaList[String]]]].toMap.mapValues(lstToPair)
      }
      else Map()
    }

    val mens = toMapIntStrings("mens", info)
    val ladies = toMapIntStrings("ladies", info)
    val youth = toMapIntStrings("youth", info)
    val pairs = toMapIntStringsPair("pairs",info)

    NationalTournament(shortName, date, mens, ladies, youth, pairs)
  }


  /**
   * Read a comite
   * @param ligue the ligue prefix
   * @param comite the comite prefix
   * @return a comite
   */
  def readComite(season: Season, ligue: String, comite: String): Comite = {
    val comiteFile = s"s$season/$ligue/$comite/comite.yml"
    logger.info(s"Read comite information in $comiteFile")
    val info = YamlParser.parseFile(comiteFile).asInstanceOf[JavaMap[String, AnyRef]].toMap
    logger.trace(s"Read $info")

    val name = info("name").asInstanceOf[String]
    val shortName = info("shortname").asInstanceOf[String]
    val clubList = info("clubs").asInstanceOf[JavaList[String]].toList

    val clubs = for (club <- clubList) yield readClub(season, ligue, comite, club)
    val coupeMap = info("coupe").asInstanceOf[JavaMap[String, Any]]

    val coupe = CoupeComite(
      YamlParser.readDate(coupeMap.get("date").asInstanceOf[String]),
      YamlParser.readLocation(coupeMap.toMap).get)
    val information = YamlParser.readInfo(s"s$season/$ligue/$comite/info.html")

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
    val clubFile = s"s$season/$ligue/$comite/$club/club.yml"
    logger.info(s"Read club information in $clubFile")
    val info = YamlParser.parseFile(clubFile).asInstanceOf[JavaMap[String, AnyRef]].toMap
    logger.trace(s"Read $info")

    val name = info("name").asInstanceOf[String]
    val shortName = info("shortname").asInstanceOf[String]
    val opens = if (info.contains("opens")) {
      val openList = info("opens").asInstanceOf[JavaList[JavaMap[String, Any]]].toList
      for (open <- openList) yield {
        OpenClub(
          YamlParser.readDate(open.get("date").asInstanceOf[String]),
          YamlParser.readLocation(open.asInstanceOf[JavaMap[String, String]].toMap).get)
      }
    } else Nil
    val teamList = info("teams").asInstanceOf[JavaList[String]].toList
    val teams = for (team <- teamList) yield readTeam(season, ligue, comite, club, team)
    val information = YamlParser.readInfo(s"s$season/$ligue/$comite/$club/info.html")
    val location = YamlParser.readLocation(info).get

    Club(name, shortName, location, opens, teams, information)
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
    val teamFile = s"s$season/$ligue/$comite/$club/$team.yml"
    logger.debug(s"Read team information in $teamFile")
    val info = YamlParser.parseFile(teamFile).asInstanceOf[JavaMap[String, AnyRef]].toMap
    logger.trace(s"Read $info")

    val name = info("name").asInstanceOf[String]
    val shortname = info("shortname").asInstanceOf[String]
    val capitain = info("capitain").asInstanceOf[String]
    val playerList = info("players").asInstanceOf[JavaList[JavaMap[String, String]]].toList
    val players = for (player <- playerList) yield createLicensedPlayer(player.toMap)
    val omit = info.contains("omit")

    Team(name, shortname, capitain, players, omit)
  }

  /**
   * Create LicensedPlayer
   * @param data data
   * @return player
   */
  def createLicensedPlayer(data: Map[String, Any]): LicensedPlayer = {
    val license = data.getOrElse("license", "license ###").asInstanceOf[String]
    val firstName = data.getOrElse("firstname", "???").asInstanceOf[String]
    val lastName = data.getOrElse("lastname", "???").asInstanceOf[String]
    val surname: Option[String] = {
      if (data.contains("surename")) YamlParser.toOption(data, "surename")
      else if (data.contains("commonname")) YamlParser.toOption(data, "commonname")
      else None
    }
    val isFeminine = data.contains("feminine")
    val isJunior = data.contains("junior")

    val emails: Set[EMail] = readEmails(data)
    val twitter: Option[String] = YamlParser.toOption(data, "twitter")
    val facebook: Option[String] = YamlParser.toOption(data, "facebook")
    val google: Option[String] = YamlParser.toOption(data, "google")

    LicensedPlayer(license, s"$lastName $firstName", surname, feminine = isFeminine, junior = isJunior,
      emails = emails, twitter = twitter, facebook = facebook, google = google)
  }

  /**
   * Read emails
   * @param data data
   * @return email
   */
  private def readEmails(data: Map[String, Any]): Set[EMail] = data.get("emails") match {
    case None => Set()
    case Some(x) =>
      var result = for (email <- x.asInstanceOf[String].split(",")) yield EMail(email.trim)
      result.toSet
  }
}

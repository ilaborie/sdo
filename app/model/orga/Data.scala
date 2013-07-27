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
    logger.info(s"Read $info")

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
    logger.info(s"Read ligue information in $comiteFile")
    val info = Yaml.load(comiteFile).asInstanceOf[JavaMap[String, AnyRef]].toMap
    logger.info(s"Read $info")

    val name = info("name").asInstanceOf[String]
    val shortName = info("shortname").asInstanceOf[String]
    val infomation = readInfo(s"data/s$currentSeason/$ligue/$comite/info.html")
    Comite(name, shortName, infomation)
  }


  /**
   * Read info file
   * @param infoFile info file
   * @return the info
   */
  def readInfo(infoFile: String): Option[Info] =
    try {
      val stream = Play.application.resourceAsStream(infoFile)

      val supplier = ByteStreams.newInputStreamSupplier(ByteStreams.toByteArray(stream))
      val input = CharStreams.newReaderSupplier(supplier, Charsets.UTF_8)
      val info = CharStreams.toString(input)
      Some(info)
    } catch {
      case _: Throwable => None
    }
}

package util

import play.Play
import com.google.common.io.Files
import com.google.common.base.Charsets
import play.api.Logger
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor
import play.api.Application
import java.io.File

/**
 * A YAML Parser
 */
case class YamlParser(app: Application) {

  private val logger = Logger("YAML")
  private val yaml = new Yaml(new CustomClassLoaderConstructor(Play.application.classloader))

  def parseFile(file: File): Any =
    try {
      logger.debug(s"Parse file: $file")
      val data = Files.toString(file, Charsets.UTF_8)
      logger.trace(s"$file content: $data")
      yaml.load(data)
    } catch {
      case t: Throwable => throw new IllegalStateException(s"Fail to parse $file", t)
    }
}

object YamlParser {
  var parser: YamlParser = null

  private def getFile(path:String): File = {
    val parent = new File(parser.app.configuration.getString("file.data").getOrElse("conf"))
    new File(parent.getAbsoluteFile + File.separator + path)
  }

  def parseFile(path: String): Any = {
    val file = getFile(path)
    if (file.exists && file.isFile) {
      parser.parseFile(file)
    } else {
      throw new IllegalArgumentException(s"File ${file.getAbsolutePath} not found")
    }
  }

  def tryParseFile(path: String): Option[Any] = try {
    Some(parseFile(path))
  } catch {
    case _: Throwable => None
  }


  /**
   * Read info file
   * @param infoFile ligue file
   * @return the ligue
   */
  def readInfo(infoFile: String): Option[String] = {
    val file = getFile(infoFile)
    if (file.exists && file.isFile) {
      Some(Files.toString(file, Charsets.UTF_8))
    } else {
      None
    }
  }
}


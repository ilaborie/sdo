package util

import play.Play
import com.google.common.io.Files
import com.google.common.base.Charsets
import play.api.Logger
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor
import play.api.Application

/**
 * A YAML Parser
 */
case class YamlParser(app: Application) {

  private val logger = Logger("YAML")
  private val yaml = new Yaml(new CustomClassLoaderConstructor(Play.application.classloader))

  def parseFile(path: String): Any = app.getExistingFile("conf/" + path) match {
    case None => throw new IllegalArgumentException(s"File $path not found")
    case Some(file) => {
      try {
        val data = Files.toString(file, Charsets.UTF_8)
        logger.trace(s"$path content: $data")
        val parsed = yaml.load(data)
        logger.debug(s"parsed: $parsed")
        parsed
      } catch {
        case t: Throwable => new IllegalStateException(s"Fail to parse $path", t)
      }
    }
  }
}

object YamlParser {
  var parser: YamlParser = null

  def parseFile(path: String): Any = parser.parseFile(path)

  def tryParseFile(path: String): Option[Any] = try {
    Some(parseFile(path))
  } catch {
    case _: Throwable => None
  }
}


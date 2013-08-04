package util

import play.Play
import java.io.InputStreamReader
import com.google.common.io.CharStreams
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

  def parseFile(path: String): Any = app.resourceAsStream(path) match {
    case None => throw new IllegalArgumentException(s"File $path not found")
    case Some(stream) => {
      val reader = new InputStreamReader(stream, Charsets.UTF_8)
      try {
        val data = CharStreams.toString(reader)
        logger.trace(s"$path content: $data")
        val parsed = yaml.load(data)
        logger.debug(s"parsed: $parsed")
        parsed
      } catch {
        case t: Throwable => new IllegalStateException(s"Fail to parse $path", t)
      } finally {
        reader.close()
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


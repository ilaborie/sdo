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
      val data = Files.toString(file, Charsets.UTF_8)
      logger.trace(s"$file content: $data")
      val parsed = yaml.load(data)
      logger.debug(s"parsed: $parsed")
      parsed
    } catch {
      case t: Throwable => throw new IllegalStateException(s"Fail to parse $file", t)
    }

}

object YamlParser {
  var parser: YamlParser = null

  def parseFile(path: String): Any = {
    val parent = new File(parser.app.configuration.getString("file.data").getOrElse("conf"))
    val file = new File(parent.getAbsoluteFile + File.separator + path)
    //if (file.exists && parent.isFile) {
      parser.parseFile(file)
    //} else {
    //  throw new IllegalArgumentException(s"File ${file.getAbsolutePath} not found")
    //}
  }

  def tryParseFile(path: String): Option[Any] = try {
    Some(parseFile(path))
  } catch {
    case _: Throwable => None
  }
}


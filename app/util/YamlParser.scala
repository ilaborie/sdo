// The MIT License (MIT)
//
// Copyright (c) 2013 Igor Laborie
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of
// this software and associated documentation files (the "Software"), to deal in
// the Software without restriction, including without limitation the rights to
// use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
// the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
// IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
// CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

package util

import java.io.{FileNotFoundException, File}
import java.util.{Map => JavaMap}

import scala.collection.JavaConversions._
import scala.io.{Source, Codec}

import play.Play
import play.api.Logger
import play.api.Application

import org.joda.time.LocalDate
import org.joda.time.format.DateTimeFormat
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor


/**
 * A YAML Parser
 */
case class YamlParser(app: Application) {
  implicit val codec = Codec.UTF8

  private val logger = Logger("YAML")
  private val yaml = new Yaml(new CustomClassLoaderConstructor(Play.application.classloader))

  def parseFile(file: File): Any =
    try {
      logger.debug(s"Parse file: $file")
      val source = Source.fromFile(file)
      try {
        yaml.load(source.reader())
      } finally {
        source.close()
      }
    } catch {
      case t: Throwable => throw new IllegalStateException(s"Fail to parse $file", t)
    }
}

object YamlParser {
  private val logger = Logger("YAML")
  var parser: YamlParser = null
  private val dateParser = DateTimeFormat.forPattern("dd-MM-yyyy")

  private def getFile(path: String): File = {
    val parent = new File(parser.app.configuration.getString("file.data").getOrElse("conf"))
    new File(parent.getAbsoluteFile + File.separator + path)
  }

  def parseFile(path: String): Any = {
    val file = getFile(path)
    if (file.exists && file.isFile) {
      parser.parseFile(file)
    } else {
      throw new FileNotFoundException(s"File ${file.getAbsolutePath} not found")
    }
  }

  def tryParseFile(path: String): Option[Any] = try {
    Some(parseFile(path))
  } catch {
    case _: FileNotFoundException => {
      logger.debug(s"File not found: $path")
      None
    }
    case e: Throwable => {
      logger.error(s"Fail reading $path", e)
      None
    }
  }

  /**
   * Read info file
   * @param infoFile ligue file
   * @return the ligue
   */
  def readInfo(infoFile: String): Option[String] = {
    val file = getFile(infoFile)
    if (file.exists && file.isFile) {
      val source = Source.fromFile(file)
      try {
        Some(source.mkString)
      } finally {
        source.close()
      }
    } else {
      None
    }
  }

  /**
   * Return an optional string
   * @param data the map
   * @param key the key
   * @return the option
   */
  def toOption(data: Map[String, Any], key: String): Option[String] = {
    if (!data.contains(key)) None
    else {
      val value = data(key)
      if (value != null) Some(value.asInstanceOf[String]) else None
    }
  }

  /**
   * Read a date
   * @param date the date as string
   * @return a local date
   */
  def readDate(date: String): LocalDate = {
    dateParser.parseDateTime(date).toLocalDate
  }

  /**
   * Read Location
   * @param data data
   * @return the location or none
   */
  def readLocation(data: Map[String, Any]): Option[Location] = {
    val oLocation = data.get("location")
    oLocation match {
      case None => None
      case Some(x) => {
        x match {
          case s: String => Some(Location(s))
          case _ => {
            val map = data("location").asInstanceOf[JavaMap[String, String]].toMap
            val name = map("name")
            val venue = toOption(map, "venue")
            val address = toAddress(venue.getOrElse(name), map, "address")
            val tel = toTelephone(map, "tel")
            Some(Location(name, venue, address, tel))
          }
        }
      }
    }
  }

  def toAddress(name: String, data: Map[String, Any], key: String): Option[Address] = {
    if (!data.contains(key)) None
    else {
      val value = data(key)
      if (value != null) Some(Address(name, value.asInstanceOf[String])) else None
    }
  }

  def toTelephone(data: Map[String, Any], key: String): Option[Telephone] = {
    if (!data.contains(key)) None
    else {
      val value = data(key)
      if (value != null) Some(Telephone(value.asInstanceOf[String])) else None
    }
  }
}

case class ParseDataException(message: String) extends RuntimeException(message)

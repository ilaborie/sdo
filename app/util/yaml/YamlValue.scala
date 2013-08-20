package util.yaml

/**
 * Tag
 */
abstract class YamlTag {
  def name: String

  def kind: String
}

case class ScalarTag(name: String, kind: String, format: String) extends YamlTag

case class NoneSpecificTag(name: String, kind: String) extends YamlTag

/**
 * Node
 */
sealed abstract class YamlValue {
  def anchor: Any = ???

  def style: Any = ???

  def spacing: Any = ???

  def lineWrapping: Any = ???
}

case class YamlArray(values: Seq[YamlValue] = List()) extends YamlValue

case class YamlScalar(value: String, formatted: Option[String] = None) extends YamlValue

case class YamlObject(fields: Seq[(YamlValue, YamlValue)]) extends YamlValue

case class YamlAlias(alias: Any) extends YamlValue

/**
 * Comments
 * @param comment comments
 */
case class YamlComment(comment: String)

/**
 * Directive
 * @param name name
 * @param parameters parameters
 */
case class YamlDirective(name: String, parameters: Any)

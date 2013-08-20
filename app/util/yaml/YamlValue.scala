package util.yaml


case class ScalarTag(name: String, kind: YamlKind, cannonicalFormat: Any) extends YamlTag

/**
 * Nodes
 */
sealed abstract class YamlValue

case class YamlArray(values: Seq[YamlValue] = List()) extends YamlValue

abstract class YamlScalar extends YamlValue {
  def valueAsString: String
}

case class YamlNumber(value: BigDecimal) extends YamlScalar {
  lazy val valueAsString = value.toString()
}

case class YamlString(value: String) extends YamlScalar {
  lazy val valueAsString = value
}

case class YamlObject(fields: Seq[(YamlValue, YamlValue)]) extends YamlValue


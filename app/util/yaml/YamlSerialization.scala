package util.yaml


sealed abstract class AbstractYamlSerializationNode {
  def node: YamlValue

  def anchor: Any
}

case class YamlSerializationNode(node: YamlValue, anchor: Any) extends AbstractYamlSerializationNode

case class YamlSerializationAlias(alias: YamlSerializationNode) extends AbstractYamlSerializationNode {
  def node = alias.node

  def anchor = alias.anchor
}

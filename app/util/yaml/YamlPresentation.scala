package util.yaml


case class YamlPresentationNonSpecificYag(name: String, kind: YamlKind) extends YamlTag

sealed abstract class AbstractYamlPresentationNode {
  def style: YamlStyle
}

case class YamlPresentationNode(node: YamlValue, style: YamlStyle) extends AbstractYamlPresentationNode

case class YamlPresentationScalarNode(node: YamlScalar, style: YamlStyle, formattedContent: Any) extends AbstractYamlPresentationNode


case class YamlPresentationComment(comment: String)



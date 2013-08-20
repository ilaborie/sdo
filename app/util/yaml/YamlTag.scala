package util.yaml


/**
 * Tag
 */
abstract class YamlTag {
  def name: String

  def kind: YamlKind
}

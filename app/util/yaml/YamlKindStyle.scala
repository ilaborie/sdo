package util.yaml

trait  YamlKind
trait  YamlScalarKind extends YamlKind
trait  YamlCollectionKind extends YamlKind
trait  YamlSequenceKind extends YamlCollectionKind
trait  YamlMappingKind extends YamlCollectionKind

trait YamlStyle
trait  YamlFlowStyle extends YamlStyle
trait  YamlBlockStyle extends YamlStyle

object YamlDoubleQuoted extends YamlScalarKind with YamlFlowStyle
object YamlSingleQuoted extends YamlScalarKind with YamlFlowStyle
object YamlPlain extends YamlScalarKind with YamlFlowStyle

object YamlLiteral extends YamlScalarKind with YamlBlockStyle
object YamlFolded extends YamlScalarKind with YamlBlockStyle

object YamlExplicit extends YamlSequenceKind with YamlMappingKind with YamlFlowStyle
object YamlSinglePair extends YamlMappingKind with YamlFlowStyle

object YamlNextLine extends YamlSequenceKind with YamlMappingKind with YamlBlockStyle
object YamlInLine extends YamlSequenceKind with YamlMappingKind with YamlBlockStyle




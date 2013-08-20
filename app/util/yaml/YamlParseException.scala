package util.yaml


abstract class YamlParseException(val message: String) extends RuntimeException(message)

case class YamlIllFormedException(override val message: String) extends YamlParseException(message)

case class YamlUnidentifiedException(override val message: String) extends YamlParseException(message)

case class YamlUnresolvedException(override val message: String) extends YamlParseException(message)

case class YamlUnrecognizedException(override val message: String) extends YamlParseException(message)

case class YamlInvalidException(override val message: String) extends YamlParseException(message)

case class YamlUnavailableException(override val message: String) extends YamlParseException(message)

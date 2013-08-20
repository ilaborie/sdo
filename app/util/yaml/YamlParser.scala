package util.yaml

/**
 * User: igorlaborie
 * Date: 20/08/13
 * Time: 21:39
 */
object YamlParser {

  def parse(yaml: String): YamlValue = ???

}

object YamlParserChar {
  private val notCPrintable: Set[Char] = {
    val allowC0 = Set(tab, cr, lf)
    val c0 = for (c <- 0x0 to 0x1F if !allowC0.contains(c.toChar)) yield c.toChar
    val c1 = for (c <- 0x80 to 0x9) yield c.toChar
    val surrogate = for (c <- 0xD800 to 0xDFFF) yield c.toChar
    c0 ++ c1 ++ surrogate ++ Set(0x7F.toChar, 0xFFFE.toChar, 0xFFFF.toChar)
  }.toSet

  def isPrintable(c: Char): Boolean = !notCPrintable.contains(c)

  private val notNBJon: Set[Char] = {
    for (c <- 0x0 to 0x1F if c != 0x9) yield c.toChar
  }.toSet

  private def isNBJon(c: Char): Boolean = !notNBJon.contains(c)

  val tab: Char = 0x9.toChar
  val space: Char = 0x20.toChar

  def isWhite(c: Char): Boolean = c == tab || c == space

  val lf: Char = 0xA.toChar
  val cr: Char = 0xD.toChar

  val bom: Char = 0xFEFF.toChar

  val sequenceEntry: Char = '-'
  val mappingKey: Char = '?'
  val mappingValue: Char = ':'

  val collectEntry: Char = ','
  val sequenceStart: Char = '['
  val sequenceEnd: Char = ']'
  val mappingStart: Char = '{'
  val mappingEnd: Char = '}'

  val comment: Char = '#'

  val anchor: Char = '&'
  val alias: Char = '*'
  val tag: Char = '!'

  val literal: Char = '|'
  val folded: Char = '>'

  val singleQuote = '\''
  val doubleQuote = '"'

  val directive = '%'

  private val reserved = Set('@', 0x60.toChar)

  def isReserved(c: Char): Boolean = reserved.contains(c)

  private val indicator = Set(sequenceEntry, mappingKey, mappingValue, collectEntry, sequenceStart, sequenceEnd,
    mappingStart, mappingEnd, comment, anchor, alias, tag, singleQuote, doubleQuote, directive) ++ reserved

  def isIndicator(c: Char): Boolean = indicator.contains(c)

  private val break = Set(cr, lf)

  def isBreak(c: Char): Boolean = break.contains(c)

  def isNotBreak(c: Char): Boolean = !break.contains(c) && !bom.equals(c) && isPrintable(c)

  def isNonSpace(c: Char): Boolean = !isWhite(c) && isNotBreak(c)

  def isDecimalDigit(c: Char): Boolean = c.isDigit

  private val hexa: Set[Char] = {
    (for (c <- 'a' to 'f') yield c) ++ (for (c <- 'A' to 'F') yield c)
  }.toSet

  def isHexaDigit(c: Char): Boolean = c.isDigit || hexa.contains(c)

  private val ascii: Set[Char] = {
    (for (c <- 'a' to 'z') yield c) ++ (for (c <- 'A' to 'Z') yield c)
  }.toSet

  def isASCII(c: Char): Boolean = ascii.contains(c)

  def isWordChar(c: Char): Boolean = isDecimalDigit(c) || isASCII(c) || c == '-'

  def indent(string: String): Int = string.takeWhile(isWhite).size

}



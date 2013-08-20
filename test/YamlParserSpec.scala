import org.specs2.mutable._

import util.yaml._

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class YamlParserSpec extends Specification {

  "YamlParser" should {

    "parse Sequence of Scalars (Exemple 2.1)" in {
      val string = """- Mark McGwire
                     |- Sammy Sosa
                     |- Ken Griffey"""
      val yaml = YamlParser.parse(string)
      yaml === YamlArray(List(
        YamlString("Mark McGwire"),
        YamlString("Sammy Sosa"),
        YamlString("Ken Griffey")
      ))
    }

    "parse Mapping Scalars to Scalars (Exemple 2.2)" in {
      val string = """hr:  65    # Home runs
                     |avg: 0.278 # Batting average
                     |rbi: 147   # Runs Batted In"""
      val yaml = YamlParser.parse(string)
      yaml === YamlObject(List(
        ("hr", YamlNumber(65)),
        ("avg", YamlNumber(0.278)),
        ("rbi", YamlNumber(147))
      ))
    }

    "parse Mapping Scalars to Sequences (Exemple 2.3)" in {
      val string = """american:
                     |  - Boston Red Sox
                     |  - Detroit Tigers
                     |  - New York Yankees
                     |national:
                     |  - New York Mets
                     |  - Chicago Cubs
                     |  - Atlanta Braves"""
      val yaml = YamlParser.parse(string)
      yaml === YamlObject(List(
      ))
    }

    "parse Sequence of Mappings (Exemple 2.4)" in {
      val string = """-
                     |  name: Mark McGwire
                     |  hr:   65
                     |  avg:  0.278
                     |-
                     |  name: Sammy Sosa
                     |  hr:   63
                     |  avg:  0.288"""
      val yaml = YamlParser.parse(string)
      yaml === YamlObject(List(
      ))
    }

    "parse Sequence of Sequences (Exemple 2.5)" in {
      val string = """- [name        , hr, avg  ]
                     |- [Mark McGwire, 65, 0.278]
                     |- [Sammy Sosa  , 63, 0.288]"""
      val yaml = YamlParser.parse(string)
      yaml === YamlObject(List(
      ))
    }

    "parse Mapping of Mappings (Exemple 2.6)" in {
      val string = """Mark McGwire: {hr: 65, avg: 0.278}
                     |Sammy Sosa: {
                     |    hr: 63,
                     |    avg: 0.288
                     |  }"""
      val yaml = YamlParser.parse(string)
      yaml === YamlObject(List(
      ))
    }

  }
}

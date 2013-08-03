package model.orga

/**
 * A season
 * @param name the name
 */
case class Season(name: String) {
  override val toString = name

}

object Season {
  val seasons: List[Season] = List("2013-2014").map(Season(_))
  val currentSeason: Season = seasons.last
}

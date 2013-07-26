package model.orga

/**
 * User: igorlaborie
 * Date: 21/07/13
 * Time: 11:19
 */
case class Club(name: String, shortName: String, comite: Comite, info: Option[Info]) {

}

object Club {
  val Rocco = Club("Rocco 6 fléches", "Rocco", Comite.Toulouse, None)
  val DDD = Club("Dubliners Darts Drinkers", "DDD", Comite.Toulouse, None)
  val X1 = Club("Double As", "X1", Comite.Toulouse, None)
  val Wood = Club("Woodpecker", "Wood", Comite.Toulouse, None)
  val Coch = Club("Cochonou's", "Coch.", Comite.Toulouse, None)
  val FT = Club("Fléches Tordues", "FT", Comite.ToulouseNord, None)
  val ONeill = Club("O'Neill", "ONeill", Comite.ToulouseNord, None)
  val DCMAG = Club("Darts club Mérignac Arlac Gironde", "DC MAG", Comite.ToulouseNord, None)

  val all = List(
    Rocco,
    DDD,
    X1,
    Wood,
    Coch,
    FT,
    ONeill,
    DCMAG
  )

  def findByComite(comite: Comite) = all.filter(_.comite == comite)

  def findByShortName(shortName: String): Option[Club] = all.filter(_.shortName == shortName) match {
    case club :: _ => Some(club)
    case Nil => None
  }
}

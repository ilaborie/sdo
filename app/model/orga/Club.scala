package model.orga

/**
 * User: igorlaborie
 * Date: 21/07/13
 * Time: 11:19
 */
case class Club(name: String, shortName: String, comite: Comite, info: Option[Info]) {

}

object Club {
  val all = List(
    Club("Rocco 6 fléches", "Rocco", Comite.Toulouse, None),
    Club("Dubliners Darts Drinkers", "Dub", Comite.Toulouse, None),
    Club("Double As", "X1", Comite.Toulouse, None),
    Club("Woodpecker", "Wood", Comite.Toulouse, None),
    Club("Cochonou's", "Coch.", Comite.Toulouse, None),
    Club("Double As", "X1", Comite.Toulouse, None),
    Club("Fléches Tordues", "FT", Comite.ToulouseNord, None),
    Club("O'Neill", "ONeill", Comite.ToulouseNord, None),
    Club("Darts club Mérignac Arlac Gironde", "DC MAG", Comite.ToulouseNord, None)
  )

  def findByComite(comite: Comite) = all.filter(_.comite == comite)
}

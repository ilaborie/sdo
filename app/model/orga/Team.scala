package model.orga

/**
 * User: igorlaborie
 * Date: 21/07/13
 * Time: 11:24
 */
case class Team(name: String, club: Club, players: Seq[LicensedPlayer]) extends Participant {
  require(players filter (_.club != club) isEmpty, "Les joueurs doivent être du même club")

  lazy val fullName = s"${club.name} - $name"
}


object Team {
  val Satanas = Team("Satanas", Club.Rocco, Nil)
  val Diabolo = Team("Diabolo", Club.Rocco, Nil)
  val DDD = Team("DDD", Club.DDD, Nil)
  val X1 = Team("X1", Club.X1, Nil)
  val Coch = Team("Coch", Club.Coch, Nil)
  val Wood = Team("Wood", Club.Wood, Nil)
  val FT = Team("FT", Club.FT, Nil)
  val ONeill = Team("ONeill", Club.ONeill, Nil)
  val DCMAG = Team("DC MAG", Club.DCMAG, Nil)

  val all = List(Satanas, Diabolo, DDD, X1, Wood, FT, ONeill, DCMAG)

  def findByName(name: String): Option[Team] = {
    ???
  }
}

package model.orga

/**
 * User: igorlaborie
 * Date: 21/07/13
 * Time: 11:24
 */
case class Team(name: String, club: Club, players: Seq[LicensedPlayer]) extends Participant {
  require(players filter (_.club != club) isEmpty, "Les joueurs doivent être du même club")
}

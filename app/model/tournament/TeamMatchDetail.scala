package model.tournament

import model.orga._

/**
 * User: igorlaborie
 * Date: 24/07/13
 * Time: 08:32
 */
case class TeamMatchDetail(team: Team, players: (Player, Player, Player, Player), substitutes: (Player, Player), doublettes: (Doublette, Doublette)) {
  // FIXME require sur les players
  // FIXME record substitue
}


class MatchDetail {
  // FIXME
  // Match Schedule
}

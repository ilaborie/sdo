package model.orga

import play.api.Logger

/**
 * Helper to read D
 */
object TournamentResultData {

  private val logger = Logger("data")

  def getNationalTournament(tournament: NationalTournament, player: Participant): Option[WinningMatch] = {
    player match {
      case p: LicensedPlayer => {
        if (!p.junior && !p.feminine) tournament.mens.get(p).map(WinningMatch)
        else if (p.feminine && !p.junior) tournament.ladies.get(p).map(WinningMatch)
        else /* if (p.junior ) */ tournament.youth.get(p).map(WinningMatch)
      }
      case d: Doublette => tournament.pairs.get(d).map(WinningMatch)
      case _ => None
    }
  }
}

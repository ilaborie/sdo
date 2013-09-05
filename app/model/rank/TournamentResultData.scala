package model.rank

import play.api.Logger

import model.orga._

/**
 * Helper to read Tournament result
 */
object TournamentResultData {

  private val logger = Logger("data")

  /**
   * Create result
   * @param player player
   * @param tournaments tournaments
   * @return result
   */
  def createResult[T<:Participant](player: T, tournaments: Seq[Tournament]): Map[Tournament, TournamentResult] = {
    for (tournament <- tournaments) yield (tournament, createResult(player, tournament))
  }.toMap

  /**
   * Create result
   * @param player player
   * @param tournament tournament
   * @return result
   */
  def createResult[T<:Participant](player: T, tournament: Tournament): TournamentResult = tournament match {
    case ol: OpenLigue => createOpenLigueResult(player, ol).getOrElse(NoParticipation)
    case cl: CoupeLigue => createCoupeLigueResult(player, cl).getOrElse(NoParticipation)
    case ml: MasterLigue => createMasterLigueResult(player, ml).getOrElse(NoParticipation)
    case ic: ComiteRank => createComiteRankResult(player, ic).getOrElse(NoParticipation)
    case nt: NationalTournament => createNationalTournamentResult(player, nt).getOrElse(NoParticipation)
    case cc: CoupeComite => createCoupeComiteResult(player, cc).getOrElse(NoParticipation)
    case oc: OpenClub => createOpenClubResult(player, oc).getOrElse(NoParticipation)
    case _ => throw new IllegalStateException(s"Cannot find result of $tournament")
  }

  // FIXME Implements
  private def createOpenLigueResult(player: Participant, ol: OpenLigue): Option[TournamentResult] = None
  private def createCoupeLigueResult(player: Participant, cl: CoupeLigue): Option[TournamentResult] = None
  private def createMasterLigueResult(player: Participant, ml: MasterLigue): Option[TournamentResult] = None
  private def createComiteRankResult(player: Participant, ic: ComiteRank): Option[TournamentResult] = None
  private def createCoupeComiteResult(player: Participant, cc: CoupeComite): Option[TournamentResult] = None
  private def createOpenClubResult(player: Participant, oc: OpenClub): Option[TournamentResult] = None

  /**
   * National result
   * @param tournament tournament
   * @param player player
   * @return result
   */
  private def createNationalTournamentResult(player: Participant, tournament: NationalTournament): Option[WinningMatch] = {
    player match {
      case p: LicensedPlayer => {
        if (!p.youth && !p.lady) tournament.mens.get(p).map(WinningMatch)
        else if (p.lady && !p.youth) tournament.ladies.get(p).map(WinningMatch)
        else /* if (p.youth ) */ tournament.youth.get(p).map(WinningMatch)
      }
      case d: Pair => tournament.pairs.get(d).map(WinningMatch)
      case _ => None
    }
  }
}

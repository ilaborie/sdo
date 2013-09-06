package model.orga

/**
 * Tournament Result
 */
sealed abstract class TournamentResult

object NoParticipation extends TournamentResult

object Winner extends TournamentResult

object RunnerUp extends TournamentResult

object SemiFinal extends TournamentResult

object QuarterFinal extends TournamentResult

object EighthFinal extends TournamentResult

object SixteenthFinal extends TournamentResult

object ThirtySecondFinal extends TournamentResult

case class RoundRobin(position: Int) extends TournamentResult

case class WinningMatch(winning: Int) extends TournamentResult


case class TournamentResults[T <: Participant](winner: Option[T],
                                               runnerUp: Option[T],
                                               semiFinal: Array[T],
                                               quarterFinal: Array[T],
                                               eighthFinal: Array[T],
                                               sixteenthFinal: Array[T],
                                               thirtySecondFinal: Array[T],
                                               groups: Option[List[List[T]]]) {
  require(semiFinal.size <= 2)
  require(quarterFinal.size <= 4)
  require(eighthFinal.size <= 8)
  require(sixteenthFinal.size <= 16)
  require(thirtySecondFinal.size <= 32)

  def getResult(player: T): Option[TournamentResult] = {
    if (winner.isDefined && winner.get == player) Some(Winner)
    else if (runnerUp.isDefined && runnerUp.get == player) Some(RunnerUp)
    else if (semiFinal.contains(player)) Some(SemiFinal)
    else if (quarterFinal.contains(player)) Some(QuarterFinal)
    else if (eighthFinal.contains(player)) Some(EighthFinal)
    else if (sixteenthFinal.contains(player)) Some(SixteenthFinal)
    else if (thirtySecondFinal.contains(player)) Some(ThirtySecondFinal)
    else if (groups.isDefined) {
      groups.get.find(group => group.contains(player)) match {
        case None => None
        case Some(group) => Some(RoundRobin(group.indexOf(player) + 1))
      }
    }
    else None
  }

  lazy val allParticipants: Seq[T] = {
    groups match {
      case Some(lst) => for (group <- lst; participant <- group) yield participant
      case None =>
        val all = winner.toSet ++ runnerUp.toSet ++ semiFinal ++
          quarterFinal ++ eighthFinal ++ sixteenthFinal ++ thirtySecondFinal
        all.toSeq
    }
  }
}




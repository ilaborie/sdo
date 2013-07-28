package model.tournament

/**
 * Tournament Result
 */
sealed abstract class TournamentResult

object Winner extends TournamentResult

object RunnerUp extends TournamentResult

object SemiFinal extends TournamentResult

object QuarterFinal extends TournamentResult

object EighthFinal extends TournamentResult

object SixteenthFinal extends TournamentResult

object ThirtySecondFinal extends TournamentResult

case class RoundRobin(position: Int) extends TournamentResult


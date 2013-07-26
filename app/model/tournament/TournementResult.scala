package model.tournament

/**
 * User: igorlaborie
 * Date: 22/07/13
 * Time: 08:34
 */
sealed abstract class TournementResult

object Winner extends TournementResult

object RunnerUp extends TournementResult

object SemiFinal extends TournementResult

object QuarterFinal extends TournementResult

object HeightFinal extends TournementResult

case class RoundRobin(position: Int) extends TournementResult


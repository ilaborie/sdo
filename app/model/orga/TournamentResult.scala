// The MIT License (MIT)
//
// Copyright (c) 2013 Igor Laborie
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of
// this software and associated documentation files (the "Software"), to deal in
// the Software without restriction, including without limitation the rights to
// use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
// the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
// IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
// CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

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
                                               semiFinal: Seq[T],
                                               quarterFinal: Seq[T],
                                               eighthFinal: Seq[T],
                                               sixteenthFinal: Seq[T],
                                               thirtySecondFinal: Seq[T],
                                               groups: Option[List[List[T]]]) {
  require(semiFinal.size <= 2)
  require(quarterFinal.size <= 4)
  require(eighthFinal.size <= 8)
  require(sixteenthFinal.size <= 16)
  require(thirtySecondFinal.size <= 32)

  def getResult(player: Participant): Option[TournamentResult] = {
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




package model.tournament

import java.util.Calendar

/**
 * User: igorlaborie
 * Date: 21/07/13
 * Time: 11:31
 */
sealed abstract class Tournament {
}

sealed abstract class LigueTournament extends Tournament

case class OpenLigue(date: Calendar) extends LigueTournament

case class CoupeLigue(date: Calendar) extends LigueTournament




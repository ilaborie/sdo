package model.orga

/**
 * User: igorlaborie
 * Date: 21/07/13
 * Time: 11:29
 */
case class Doublette(player1: Player, player2: Player) extends Participant{
  require(player1 != player2, "Deux joueurs différent dans une doublette")

}

package model.orga

/**
 * User: igorlaborie
 * Date: 21/07/13
 * Time: 11:19
 */
case class Club(name: String, shortName: String, teams: Seq[Team], info: Option[Info]) {
}

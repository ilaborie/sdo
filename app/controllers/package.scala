import model.orga._
import play.api.mvc._

/**
 * User: igorlaborie
 * Date: 28/07/13
 * Time: 09:34
 */
package object controllers {

  /**
   * A Ligue action
   * @param ligueShortName the ligue short name
   * @param fun a function
   * @return the results
   */
  case class LigueAction(val ligueShortName: String)(val fun: Ligue => Result) {
    val result = Ligue.findByShortName(ligueShortName) match {
      case Some(ligue) => fun(ligue)
      case _ => Results.BadRequest(s"Ligue non connue: $ligueShortName")
    }
  }


  /**
   * A comité action
   * @param ligueShortName the ligue short name
   * @param comiteShortName the comite short name
   * @param fun a function
   * @return the results
   */
  case class ComiteAction(val ligueShortName: String, val comiteShortName: String)(val fun: (Ligue, Comite) => Result) {
    val result = Ligue.findByShortName(ligueShortName) match {
      case Some(ligue) => ligue.comites.find(_.shortName == comiteShortName) match {
        case Some(comite) => fun(ligue, comite)
        case _ => Results.BadRequest(s"Comite non connue: $comiteShortName dans la ligue $ligue")
      }
      case _ => Results.BadRequest(s"Ligue non connue: $ligueShortName")
    }
  }

}

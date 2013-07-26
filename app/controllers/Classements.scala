package controllers

import play.api.mvc._
import model.rank.SeasonRanking
import model.orga.Ligue

/**
 * Mains pages
 */
object Classements extends Controller {
  /**
   * Classementpage
   * @param ligueShortName ligue short name
   * @param comiteShortName comite short name
   * @return the comite page
   */
  def team(ligueShortName: String, comiteShortName: String) = Action {
    val findLigue = Ligue.findByShortName(ligueShortName)
    findLigue match {
      case Some(_) => {
        val ligue = findLigue.get
        val findComite = ligue.comites.find(_.shortName == comiteShortName)
        findComite match {
          case Some(_) => {
            val comite = findComite.get
            Ok(views.html.classement.team(ligue, comite, SeasonRanking.forComite(comite)))
          }
          case _ => BadRequest(s"Comite non connue: $comiteShortName dans la ligue $ligue")
        }
      }
      case _ => BadRequest(s"Ligue non connue: $ligueShortName")
    }
  }


}

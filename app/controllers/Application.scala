package controllers

import play.api.mvc._
import model.orga.Ligue

/**
 * Mains pages
 */
object Application extends Controller {

  /**
   * Index page
   * @return the index page
   */
  def index = ligues

  /**
   * Ligues Pages
   * @return ligues page
   */
  def ligues = Action {
    Ok(views.html.ligues(Ligue.all))
  }

  /**
   * Ligue page
   * @param shortName the ligue short name
   * @return the ligue page
   */
  def ligue(shortName: String) = Action {
    val find = Ligue.findByShortName(shortName)
    find match {
      case Some(_) => Ok(views.html.ligue(find.get))
      case _ => BadRequest(s"Ligue non connue: $shortName")
    }
  }

  /**
   * Comite page
   * @param ligueShortName ligue short name
   * @param comiteShortName comite short name
   * @return the comite page
   */
  def comite(ligueShortName: String, comiteShortName: String) = Action {
    val findLigue = Ligue.findByShortName(ligueShortName)
    findLigue match {
      case Some(_) => {
        val ligue = findLigue.get
        val findComite = ligue.comites.find(_.shortName == comiteShortName)
        findComite match {
          case Some(_) => Ok(views.html.comite(ligue, findComite.get))
          case _ => BadRequest(s"Comite non connue: $comiteShortName dans la ligue $ligue")
        }
      }
      case _ => BadRequest(s"Ligue non connue: $ligueShortName")
    }
  }


}

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
    Ligue.findByShortName(shortName) match {
      case Some(ligue) => Ok(views.html.ligue(ligue))
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
    Ligue.findByShortName(ligueShortName) match {
      case Some(ligue) => ligue.comites.find(_.shortName == comiteShortName) match {
        case Some(comite) => Ok(views.html.comite(ligue, comite))
        case _ => BadRequest(s"Comite non connue: $comiteShortName dans la ligue $ligue")
      }
      case _ => BadRequest(s"Ligue non connue: $ligueShortName")
    }
  }

  def club(ligueShortName: String, comiteShortName: String, clubShortName: String) = Action {
    Ligue.findByShortName(ligueShortName) match {
      case Some(ligue) => ligue.comites.find(_.shortName == comiteShortName) match {
        case Some(comite) => comite.findClubByShortName(clubShortName) match {
          case Some(club) => Ok(views.html.club(ligue, comite, club))
          case _ => BadRequest(s"Club non connue: $clubShortName dans le comité $comite")
        }
        case _ => BadRequest(s"Comite non connue: $comiteShortName dans la ligue $ligue")
      }

      case _ => BadRequest(s"Ligue non connue: $ligueShortName")
    }
  }
}

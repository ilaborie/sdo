package controllers

import play.api.mvc._
import model.orga._

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
    Ok(views.html.ligues(Ligue.ligues))
  }

  /**
   * Ligue page
   * @param shortName the ligue short name
   * @return the ligue page
   */
  def ligue(shortName: String) = Action {
    LigueAction(shortName) {
      (ligue: Ligue) => Ok(views.html.ligue(ligue))
    }.result
  }

  /**
   * Comite page
   * @param ligueShortName ligue short name
   * @param comiteShortName comite short name
   * @return the comite page
   */
  def comite(ligueShortName: String, comiteShortName: String) = Action {
    ComiteAction(ligueShortName, comiteShortName) {
      (ligue: Ligue, comite: Comite) => Ok(views.html.comite(ligue, comite))
    }.result
  }

  /**
   * Club page
   * @param ligueShortName ligue short name
   * @param comiteShortName comite short name
   * @param clubShortName club short name
   * @return the club page
   */
  def club(ligueShortName: String, comiteShortName: String, clubShortName: String) = Action {
    ComiteAction(ligueShortName, comiteShortName) {
      (ligue: Ligue, comite: Comite) => comite.findClubByShortName(clubShortName) match {
        case Some(club) => Ok(views.html.club(ligue, comite, club))
        case _ => BadRequest(s"Club non connue: $clubShortName dans le comité $comite")
      }
    }.result
  }
}

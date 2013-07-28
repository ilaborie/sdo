package controllers

import play.api.mvc._
import model.orga._
import model.rank._


/**
 * Classements pages
 */
object Classements extends Controller {

  /**
   * Comite Team
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return team ranking
   */
  def comiteTeam(ligueShortName: String, comiteShortName: String) = Action {
    ComiteAction(ligueShortName, comiteShortName) {
      (ligue: Ligue, comite: Comite) =>
        Ok(views.html.classement.team(ligue, comite, ComiteRanking.team(comite)))
    }.result
  }

  /**
   * Comite Single
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return single ranking
   */
  def comiteSingle(ligueShortName: String, comiteShortName: String) = Action {
    ComiteAction(ligueShortName, comiteShortName) {
      (ligue: Ligue, comite: Comite) =>
        Ok(views.html.classement.single(ligue, comite, ComiteRanking.single(comite)))
    }.result
  }


  /**
   * Comite Junior
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return junior ranking
   */
  def comiteJunior(ligueShortName: String, comiteShortName: String) = Action {
    ComiteAction(ligueShortName, comiteShortName) {
      (ligue: Ligue, comite: Comite) =>
        Ok(views.html.classement.junior(ligue, comite, ComiteRanking.junior(comite)))
    }.result
  }

  /**
   * Comite Feminine
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return feminine ranking
   */
  def comiteFeminine(ligueShortName: String, comiteShortName: String) = Action {
    ComiteAction(ligueShortName, comiteShortName) {
      (ligue: Ligue, comite: Comite) =>
        Ok(views.html.classement.feminine(ligue, comite, ComiteRanking.feminine(comite)))
    }.result
  }

  /**
   * Comite Doublette
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return doublette ranking
   */
  def comiteDoublette(ligueShortName: String, comiteShortName: String) = Action {
    ComiteAction(ligueShortName, comiteShortName) {
      (ligue: Ligue, comite: Comite) =>
        Ok(views.html.classement.double(ligue, comite, ComiteRanking.double(comite)))
    }.result
  }
}

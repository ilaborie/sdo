package controllers

import play.api.mvc._
import model.orga._
import model.rank._
import model.team.TeamChampionship


/**
 * Classements pages
 */
object Classements extends Controller {

  private val season: Season = Data.currentSeason

  /**
   * Ligue Single
   * @param ligueShortName ligue
   * @return single ranking
   */
  def ligueSingle(ligueShortName: String) = Action {
    LigueAction(ligueShortName) {
      (ligue: Ligue) =>
        Ok(views.html.classement.ligue.single(ligue, LigueRanking.single(ligue)))
    }.result
  }

  /**
   * Ligue Feminine
   * @param ligueShortName ligue
   * @return feminine ranking
   */
  def ligueFeminine(ligueShortName: String) = Action {
    LigueAction(ligueShortName) {
      (ligue: Ligue) =>
        Ok(views.html.classement.ligue.feminine(ligue, LigueRanking.feminine(ligue)))
    }.result
  }

  /**
   * Ligue Junior
   * @param ligueShortName ligue
   * @return junior ranking
   */
  def ligueJunior(ligueShortName: String) = Action {
    LigueAction(ligueShortName) {
      (ligue: Ligue) =>
        Ok(views.html.classement.ligue.junior(ligue, LigueRanking.junior(ligue)))
    }.result
  }

  /**
   * Ligue Double
   * @param ligueShortName ligue
   * @return double ranking
   */
  def ligueDoublette(ligueShortName: String) = Action {
    LigueAction(ligueShortName) {
      (ligue: Ligue) =>
        Ok(views.html.classement.ligue.double(ligue, LigueRanking.double(ligue)))
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
        Ok(views.html.classement.comite.single(ligue, comite, ComiteRanking.single(comite)))
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
        Ok(views.html.classement.comite.feminine(ligue, comite, ComiteRanking.feminine(comite)))
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
        Ok(views.html.classement.comite.junior(ligue, comite, ComiteRanking.junior(comite)))
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
        Ok(views.html.classement.comite.double(ligue, comite, ComiteRanking.double(comite)))
    }.result
  }

  /**
   * Comite Team
   * @param ligueShortName ligue
   * @param comiteShortName comite
   * @return team ranking
   */
  def comiteTeam(ligueShortName: String, comiteShortName: String) = Action {
    ComiteAction(ligueShortName, comiteShortName) {
      (ligue: Ligue, comite: Comite) =>
        Ok(views.html.classement.comite.team(ligue, comite, ComiteRanking.team(comite)))
    }.result
  }
}

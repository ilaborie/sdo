package controllers


import play.mvc.Controller
import securesocial.core.SecureSocial

import model.orga._
import model.user._


/**
 * User controller
 */
object Users extends Controller with SecureSocial {

  private val season: Season = Season.currentSeason

  def profile() = SecuredAction {
    implicit request =>
      val identity = request.user
      val user = User(identity)
      user match {
        case lu: LocalUser => Ok(views.html.user.profile(lu, season))
        case _ => Forbidden("Not a local user")
      }
  }

  def admin() = SecuredAction {
    implicit request =>
      val identity = request.user
      val user = User(identity)
      if (user.isAdmin) {
        val players = Ligue.players ++ Ligue.nlPlayers
        val pairs = for {
          ligue <- Ligue.ligues
          tour <- ligue.tournaments
          pair <- tour.getPairs
        } yield pair

        val allPairs: List[Pair] = pairs.toSet.toList

        Ok(views.html.user.admin(players, allPairs.sortBy(_.name)))
      }
      else Forbidden("Only Administrator Granted !")
  }
}

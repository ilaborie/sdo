// The MIT License (MIT)
//
// Copyright (c) 2013 Igor Laborie
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of
// this software and associated documentation files (the "Software"), to deal in
// the Software without restriction, including without limitation the rights to
// use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
// the Software, and to permit persons to whom the Software is furnished to do so,
// subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
// FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
// COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
// IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
// CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

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

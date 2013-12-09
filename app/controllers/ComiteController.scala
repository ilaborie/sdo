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

import play.api.mvc._
import model.orga.{Comite, Ligue}
import securesocial.core.{SecuredRequest, Identity, SecureSocial}
import scala.concurrent.{Await, Future}

import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._

trait ComiteController extends SecureSocial {

  def SecuredComiteAction(ligueShortName: String, comiteShortName: String, ajaxCall: Boolean = false)(f: (Comite, SecuredRequest[AnyContent]) => Result) = SecuredAction(ajaxCall) {
    implicit request => {
      Ligue.findByShortName(ligueShortName) match {
        case Some(ligue) => ligue.comites.find(_.shortName == comiteShortName) match {
          case Some(comite) => f(comite, request)
          case _ => BadRequest(s"Comite non connue: $comiteShortName dans la ligue $ligue")
        }
        case _ => BadRequest(s"Ligue non connue: $ligueShortName")
      }
    }
  }

  def SecuredComiteAsyncAction(ligueShortName: String, comiteShortName: String, ajaxCall: Boolean = false)(f: (Comite, Identity) => Future[SimpleResult]) = SecuredAction(ajaxCall) {
    implicit request => {
      val res = Ligue.findByShortName(ligueShortName) match {
        case Some(ligue) => ligue.comites.find(_.shortName == comiteShortName) match {
          case Some(comite) => f(comite, request.user)
          case _ => Future(BadRequest(s"Comite non connue: $comiteShortName dans la ligue $ligue"))
        }
        case _ => Future(BadRequest(s"Ligue non connue: $ligueShortName"))
      }
      Await.result(res, 1.seconds)
    }
  }

  def ComiteAction(ligueShortName: String, comiteShortName: String)(f: Comite => Result) = Action {
    implicit request => {
      Ligue.findByShortName(ligueShortName) match {
        case Some(ligue) => ligue.comites.find(_.shortName == comiteShortName) match {
          case Some(comite) => f(comite)
          case _ => BadRequest(s"Comite non connue: $comiteShortName dans la ligue $ligue")
        }
        case _ => BadRequest(s"Ligue non connue: $ligueShortName")
      }
    }
  }

  def ComiteAsyncAction(ligueShortName: String, comiteShortName: String)(f: Comite => Future[SimpleResult]) = Action.async {
    val res = Ligue.findByShortName(ligueShortName) match {
      case Some(ligue) => ligue.comites.find(_.shortName == comiteShortName) match {
        case Some(comite) => f(comite)
        case _ => Future(BadRequest(s"Comite non connue: $comiteShortName dans la ligue $ligue"))
      }
      case _ => Future(BadRequest(s"Ligue non connue: $ligueShortName"))
    }
    res
  }
}

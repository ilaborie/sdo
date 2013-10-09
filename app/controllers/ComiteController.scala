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

package controllers

import play.api.mvc._
import model.orga.Ligue
import securesocial.core.{SecuredRequest, Identity, SecureSocial}
import scala.concurrent.{Await, Future}

import scala.concurrent.duration._
import play.api.libs.concurrent.Execution.Implicits._

trait LigueController extends SecureSocial {

  def SecuredLigueAction(ligueShortName: String, ajaxCall: Boolean = false)(f: (Ligue, SecuredRequest[AnyContent]) => Result) = SecuredAction(ajaxCall) {
    implicit request => {
      Ligue.findByShortName(ligueShortName) match {
        case Some(ligue) => f(ligue, request)
        case _ => Results.BadRequest(s"Ligue non connue: $ligueShortName")
      }
    }
  }

  def SecuredLigueAsyncAction(ligueShortName: String, ajaxCall: Boolean = false)(f: (Ligue, Identity) => Future[SimpleResult]) = SecuredAction(ajaxCall) {
    implicit request => {
      val res = Ligue.findByShortName(ligueShortName) match {
        case Some(ligue) => f(ligue, request.user)
        case _ => Future(Results.BadRequest(s"Ligue non connue: $ligueShortName"))
      }
      Await.result(res, 1.seconds)
    }
  }

  def LigueAction(ligueShortName: String)(f: Ligue => Result) = Action {
    implicit request => {
      Ligue.findByShortName(ligueShortName) match {
        case Some(ligue) => f(ligue)
        case _ => Results.BadRequest(s"Ligue non connue: $ligueShortName")
      }
    }
  }

  def LigueAsyncAction(ligueShortName: String)(f: Ligue => Future[SimpleResult]) = Action.async {
    implicit request => {
      val res: Future[SimpleResult] = Ligue.findByShortName(ligueShortName) match {
        case Some(ligue) => f(ligue)
        case _ => Future(Results.BadRequest(s"Ligue non connue: $ligueShortName"))
      }
      res
    }
  }
}

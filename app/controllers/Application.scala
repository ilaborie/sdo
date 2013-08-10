package controllers

import play.api.mvc._

import securesocial.core._

/**
 * Mains pages
 */
object Application extends Controller with SecureSocial {

  // a sample action using the new authorization hook
  def onlyTwitter = SecuredAction(WithProvider("twitter")) {
    implicit request =>
    //
    //    Note: If you had a User class and returned an instance of it from UserService, this
    //          is how you would convert Identity to your own class:
    //
    //    request.user match {
    //      case user: User => // do whatever you need with your user class
    //      case _ => // did not get a User instance, should not happen,log error/thow exception
    //    }
      Ok("You can see this because you logged in using Twitter")
  }

  // An Authorization implementation that only authorizes uses that logged inputFieldConstructor using twitter
  case class WithProvider(provider: String) extends Authorization {
    def isAuthorized(user: Identity) = {
      user.identityId.providerId == provider
    }
  }

}

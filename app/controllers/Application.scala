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
      user.userIdFromProvider.providerId == provider
    }
  }

}

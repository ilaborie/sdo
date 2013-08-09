package plugin

import securesocial.controllers.TemplatesPlugin
import play.api.templates.{Txt, Html}
import play.api.mvc.{Request, RequestHeader}
import play.api.data.Form
import securesocial.controllers.Registration.RegistrationInfo
import securesocial.core.{Identity, SecuredRequest}
import securesocial.controllers.PasswordChange.ChangeInfo

/**
 * Custom Template plugin
 */
class CustomTemplatesPlugin(application: play.api.Application) extends TemplatesPlugin {
  override def getLoginPage[A](implicit request: Request[A], form: Form[(String, String)],
                               msg: Option[String] = None): Html = {
    views.html.custom.login(form, msg)
  }

  override def getSignUpPage[A](implicit request: Request[A], form: Form[RegistrationInfo], token: String): Html = {
    views.html.custom.Registration.signUp(form, token)
  }

  override def getStartSignUpPage[A](implicit request: Request[A], form: Form[String]): Html = {
    views.html.custom.Registration.startSignUp(form)
  }

  override def getStartResetPasswordPage[A](implicit request: Request[A], form: Form[String]): Html = {
    views.html.custom.Registration.startResetPassword(form)
  }

  def getResetPasswordPage[A](implicit request: Request[A], form: Form[(String, String)], token: String): Html = {
    views.html.custom.Registration.resetPasswordPage(form, token)
  }

  def getPasswordChangePage[A](implicit request: SecuredRequest[A], form: Form[ChangeInfo]): Html = {
    views.html.custom.passwordChange(form)
  }

  def getNotAuthorizedPage[A](implicit request: Request[A]): Html = {
    views.html.custom.notAuthorized()
  }

  def getSignUpEmail(token: String)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.custom.mails.signUpEmail(token)))
  }

  def getAlreadyRegisteredEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.custom.mails.alreadyRegisteredEmail(user)))
  }

  def getWelcomeEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.custom.mails.welcomeEmail(user)))
  }

  def getUnknownEmailNotice()(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.custom.mails.unknownEmailNotice(request)))
  }

  def getSendPasswordResetEmail(user: Identity, token: String)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.custom.mails.passwordResetEmail(user, token)))
  }

  def getPasswordChangedNoticeEmail(user: Identity)(implicit request: RequestHeader): (Option[Txt], Option[Html]) = {
    (None, Some(views.html.custom.mails.passwordChangedNotice(user)))
  }
}

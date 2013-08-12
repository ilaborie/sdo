package service

import securesocial.core._
import play.api.mvc.{Session, RequestHeader}
import play.api.{Application, Logger}

/**
 * A sample event listener
 */
class AuthEventListener(app: Application) extends EventListener {
  override def id: String = "AuthEventListener"

  private val logger = Logger("Authentication")

  def onEvent(event: Event, request: RequestHeader, session: Session): Option[Session] = {
    val eventName = event match {
      case e: LoginEvent => "login"
      case e: LogoutEvent => "logout"
      case e: SignUpEvent => "signup"
      case e: PasswordResetEvent => "password reset"
      case e: PasswordChangeEvent => "password change"
    }

    logger.info(s"traced $eventName event for user ${event.user.fullName}")
    // Not changing the session so just return None
    // if you wanted to change the session then you'd do something like
    // Some(session + ("your_key" -> "your_value"))
    None
  }
}

package util

import play.api.templates.Html

import play.libs.Akka
import play.api.{Logger, Play}
import Play.current

/**
 * Mailer
 */
object Mailer {
  val logger = Logger("Mailer")
  val fromAddress = current.configuration.getString("smtp.from").get

  def sendEmail(subject: String, recipient: String, body: Html) {
    import com.typesafe.plugin._
    import scala.concurrent.duration._
    import play.api.libs.concurrent.Execution.Implicits._

    if (Logger.isDebugEnabled) {
      Logger.debug("Sending email to %s".format(recipient))
      Logger.debug("Mail = [%s]".format(body))
    }

    Akka.system.scheduler.scheduleOnce(1 seconds) {
      val mail = use[MailerPlugin].email
      mail.setSubject(subject)
      mail.addRecipient(recipient)
      mail.addFrom(fromAddress)
      // the mailer plugin handles null / empty string gracefully
      mail.send("", body.body)
    }
  }

}

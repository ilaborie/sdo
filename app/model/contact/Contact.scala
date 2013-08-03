package model.contact

import model.orga.Season

/**
 * Contact
 * @param name a name
 * @param email maybe an email address
 * @param url maybe an URL
 * @param info maybe some ligue
 */
case class Contact(name: String, email: Option[EMail], url: Option[String], info: Option[Info])


object Contact {
  lazy val contacts: Seq[Contact] = DataContact.readContacts(Season.currentSeason)
}

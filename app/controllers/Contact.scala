package controllers

import play.api.mvc._

/**
 * Mains pages
 */
object Contact extends Controller {

  /**
   * Show contacts
   * @return contacts page
   */
  def contact() = Action {
    Ok(views.html.contacts(model.contact.Contact.contacts))
  }

}

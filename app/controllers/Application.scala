package controllers

import play.api.mvc._
import model.orga.Ligue

object Application extends Controller {

  def index = Action {
    Ok(views.html.ligue(Ligue.SudOuest))
  }

}

package controllers

import https.{Secured}
import play.api.mvc._

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }

//  def authenticated = BasicAuthAction { request =>
//    Ok("Authenticated the user")
//  }

  def authenticated = Secured.BasicAuth {
    Action { request =>
      Ok("Authenticated the user")
    }
  }

}
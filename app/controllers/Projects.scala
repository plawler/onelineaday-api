package controllers

import java.util.Date

import models.Project
import play.api.libs.json.{JsError, Json}
import play.api.mvc.{Action, Controller}

/**
 * Created By: paullawler
 */
object Projects extends Controller {

  // https://stackoverflow.com/questions/17040852/json-writes-in-play-2-1-1
  implicit val projectJson = Json.format[Project]

  def post = Action(parse.json) { request =>
    request.body.validate[Project].map {
      case project => Ok(Json.toJson(project))
    }.recoverTotal {
      e => BadRequest("Error:" + JsError.toFlatJson(e))
    }
  }

  def get(id: Long) = Action { request =>
    find(Some(id)) match {
      case project => Ok(Json.toJson(project))
      case None => BadRequest("Invalid resource")
    }
  }

  def put(id: Long) = Action(parse.json) { request =>
    request.body.validate[Project].map {
      case project =>
        find(project.id) match {
          case Some(p) =>
            val update = Project (p.id, p.userId, p.name, p.description, p.createdOn, p.retiredOn)
            Ok (Json.toJson (update) )
          case None => BadRequest("Invalid resource")
        }
    }.recoverTotal {
      e => BadRequest("Error:" + JsError.toFlatJson(e))
    }
  }

  def patch(id: Long) = TODO // only accept retireOn. implement as tupled json read

  private def find(id: Option[Long]): Option[Project] =
    if (id == Some(12345678))
      Option(Project(Some(12345678), 12345678, "The One Line a Day API", "An API is born", new Date(), None))
    else None

}

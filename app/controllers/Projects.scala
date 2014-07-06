package controllers

import java.util.Date

import formatters.json.DateFormatter.JsonDateFormatter
import models.Project
import org.joda.time.format.DateTimeFormat
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

/**
 * Created By: paullawler
 */
object Projects extends Controller {

  // https://stackoverflow.com/questions/17040852/json-writes-in-play-2-1-1
//  implicit val projectJson = Json.format[Project]
  implicit val projectPatch = (__ \ "retiredOn").read[String]

  val format = DateTimeFormat.forPattern("yyyy-MM-dd")

  def post = Action(parse.json) { request =>
    request.body.validate[Project].map {
      case project => Ok(Json.toJson(project))
    }.recoverTotal {
      e => BadRequest("Error:" + JsError.toFlatJson(e))
    }
  }

  def get(id: Long) = Action { request =>
    val project = find(id)
    if (project.isEmpty) BadRequest("Invalid resource")
    else Ok(Json.toJson(project))
  }

  def put(id: Long) = Action(parse.json) { request =>
    request.body.validate[Project].map {
      case project =>
        find(project.id.get) match {
          case Some(p) =>
            val update = Project(p.id, p.userId, project.name, project.description, p.createdOn, p.retiredOn)
            Ok(Json.toJson(update))
          case None => BadRequest("Invalid resource")
        }
    }.recoverTotal {
      e => BadRequest("Error:" + JsError.toFlatJson(e))
    }
  }

  def patch(id: Long) = Action(parse.json) { request =>
    request.body.validate[(String)].map { retiredOn =>
      find(id) match {
        case Some(project) =>
          val patched = Project(project.id, project.userId, project.name, project.description, project.createdOn, Some(format.parseDateTime(retiredOn).toDate))
          Ok(Json.toJson(patched))
        case None => BadRequest("Invalid resource")
      }
    }.recoverTotal {
      e => BadRequest("Error:" + JsError.toFlatJson(e))
    }
  }

  private def find(id: Long): Option[Project] =
    if (id == 12345678)
      Option(Project(Some(id), 12345678, "The One Line a Day API", "An API is born", new Date(), None))
    else None

}

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
  // implicit val projectJson = Json.format[Project]
  implicit val projectPatch = (__ \ "retiredOn").read[String]

  val format = DateTimeFormat.forPattern("yyyy-MM-dd")

  def post = Action(parse.json) { request =>
    request.body.validate[Project].map {
      case project => Ok(Json.toJson(project))
    }.recoverTotal {
      e => BadRequest("Error:" + JsError.toFlatJson(e))
    }
  }

  def get(id: String) = Action { request =>
    val project = find(id)
    if (project.isEmpty) BadRequest("Invalid resource")
    else Ok(Json.toJson(project))
  }

  def put(id: String) = Action(parse.json) { request =>
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

  def patch(id: String) = Action(parse.json) { request =>
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

  private def find(id: String): Option[Project] =
    if (id == "d9227b5f-05e6-11e4-9180-cd98919f6869")
      Option(Project(Some("d9227b5f-05e6-11e4-9180-cd98919f6869"), "d9220627-05e6-11e4-9180-6fad63942f7f", "The One Line a Day API", "An API is born", new Date(), None))
    else None

}

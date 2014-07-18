package controllers

import java.util.Date

import com.fasterxml.uuid.Generators
import formatters.json.DateFormatter.JsonDateFormatter
import https.Secured
import models.Project
import org.joda.time.format.DateTimeFormat
import play.api.Logger
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

/**
 * Created By: paullawler
 */
object Projects extends Controller {

  val logger = Logger(this.getClass)

  // https://stackoverflow.com/questions/17040852/json-writes-in-play-2-1-1

  // implicit val projectJson = Json.format[Project]
  implicit val projectPatch = (__ \ "retiredOn").read[String]

  val format = DateTimeFormat.forPattern("yyyy-MM-dd")

  def post = Secured.BasicAuth {
    Action(parse.json) { request =>
      request.body.validate[Project].map { p =>
        val projectId = Generators.timeBasedGenerator().generate().toString
        val project = Project(Some(projectId), p.userId, p.name, p.description, p.createdOn, None)
        Project.create(project)
        Ok(Json.toJson(project))
      }.recoverTotal {
        e => BadRequest("Error:" + JsError.toFlatJson(e))
      }
    }
  }

  def get(id: String) = Secured.BasicAuth {
    Action { request =>
      val project = Project.find(id)
      if (project.isEmpty) BadRequest("Invalid resource")
      else Ok(Json.toJson(project))
    }
  }

  def all() = Action { request =>
    val projects = for (project <- Project.findAll("1234")
    ) yield Json.toJson(project)
    Ok(Json.arr(projects))
  }

  def put(id: String) = Action(parse.json) { request =>
    request.body.validate[Project].map {
      case project =>
        Project.find(project.id.get) match {
          case Some(p) =>
            val updated = Project(p.id, p.userId, project.name, project.description, p.createdOn, p.retiredOn)
            Project.update(updated)
            Ok(Json.toJson(updated))
          case None => BadRequest("Invalid resource")
        }
    }.recoverTotal {
      e => BadRequest("Error:" + JsError.toFlatJson(e))
    }
  }

  def patch(id: String) = Action(parse.json) { request =>
    request.body.validate[(String)].map { retiredOn =>
      Project.find(id) match {
        case Some(project) =>
          val patched = Project(project.id, project.userId, project.name, project.description, project.createdOn, Some(format.parseDateTime(retiredOn).toDate))
          Project.update(patched)
          Ok(Json.toJson(patched))
        case None => BadRequest("Invalid resource")
      }
    }.recoverTotal {
      e => BadRequest("Error:" + JsError.toFlatJson(e))
    }
  }

}

package models

import java.util.{UUID, Date}
import anorm.SqlParser._
import anorm._
import formatters.json.DateFormatter.JsonDateFormatter
import play.api.db.DB
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Play.current

import utils.UUIDHelper._

/**
 * Created By: paullawler
 */

case class Project(id: Option[String], userId: String, name: String, description: String, createdOn: Date, retiredOn: Option[Date]) {
  def retired: Boolean = retiredOn.nonEmpty
}

case class ProjectModel(id: UUID, userId: UUID, name: String, description: String, createdOn: Date, retiredOn: Option[Date]) {
  def retired: Boolean = retiredOn.nonEmpty
}

object Project {

  //  http://heydev.com/2013/11/22/play-rest-api-contd/

  // http://eng.kifi.com/working-with-json-in-play-2-1/
  implicit val dateFormat: Format[Date] = JsonDateFormatter

  implicit val reads = (
    (__ \ "id").readNullable[String] and
    (__ \ "userId").read[String] and
    (__ \ "name").read[String] and
    (__ \ "description").read[String] and
    (__ \ "createdOn").read[Date] and
    (__ \ "retiredOn").readNullable[Date]
  )(Project.apply _)

  implicit val writes = (
    (__ \ "id").writeNullable[String] and
    (__ \ "userId").write[String] and
    (__ \ "name").write[String] and
    (__ \ "description").write[String] and
    (__ \ "createdOn").write[Date] and
    (__ \ "retiredOn").writeNullable[Date]
  )(unlift(Project.unapply))

  val projectParser = {
      get[UUID]("id") ~
      get[UUID]("user_id") ~
      get[String]("name") ~
      get[String]("description") ~
      get[Date]("created_on") ~
      get[Option[Date]]("retired_on") map {
      case id~userId~name~description~created_on~retiredOn => Project(Some(id.toString), userId.toString, name, description, created_on, retiredOn)
    }
  }

  def create(project: Project) = DB.withConnection { implicit conn =>
    SQL(
      """
      insert into projectz (id, name, description, created_on, user_id)
      values ({id}, {name}, {description}, {created_on}, {userId})
      """
    ).on(
        'id -> project.id.get, 'name -> project.name, 'description -> project.description, 'created_on -> project.createdOn,
        'userId -> project.userId
      ).executeUpdate
  }

  def find(id: String): Option[Project] = DB.withConnection { implicit conn =>
      SQL("select * from projectz where id = {id}").on('id -> id).as(projectParser.singleOpt)
  }

  def update(project: Project) = DB.withConnection { implicit conn =>
    SQL(
      """
      update projectz set name = {name}, description = {description}
      where id = {id}
      """
    ).on(
        'name -> project.name, 'description -> project.description, 'id -> project.id
      ).executeUpdate
  }

  def findAll(userId: String): Seq[Project] = DB.withConnection { implicit conn =>
    SQL(
      """
        select * from projectz where user_id = {userId} order by created_on desc
        """
    ).on('userId -> userId).as(projectParser *)
  }

}

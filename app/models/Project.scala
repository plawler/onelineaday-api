package models

import java.util.Date
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Created By: paullawler
 */

case class Project(id: Option[Long], userId: Long, name: String, description: String, createdOn: Date, retiredOn: Option[Date]) {
  def retired: Boolean = retiredOn.nonEmpty
}

object Project {

//  http://heydev.com/2013/11/22/play-rest-api-contd/

//  implicit val reads = (
//    (__ \ "id").read[Option[Long]] and
//    (__ \ "name").read[String] and
//    (__ \ "description").read[String] and
//    (__ \ "createdOn").read[Date] and
//    (__ \ "retiredOn").readNullable[Date]
//  )(Project.apply _)
//
//  implicit val writes = (
//    (__ \ "id").write[Option[Long]] and
//    (__ \ "name").write[String] and
//    (__ \ "description").write[String] and
//    (__ \ "createdOn").write[Date] and
//    (__ \ "retiredOn").writeNullable[Date]
//  )(unlift(Project.unapply))

}

package models

import java.util.Date
import formatters.json.DateFormatter.JsonDateFormatter
import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Created By: paullawler
 */

case class Project(id: Option[String], userId: String, name: String, description: String, createdOn: Date, retiredOn: Option[Date]) {
  def retired: Boolean = retiredOn.nonEmpty
}

object Project {
  //  http://heydev.com/2013/11/22/play-rest-api-contd/

  // http://eng.kifi.com/working-with-json-in-play-2-1/
  implicit val dateFormat: Format[Option[Date]] = JsonDateFormatter

  implicit val reads = (
    (__ \ "id").read[Option[String]] and
    (__ \ "userId").read[String] and
    (__ \ "name").read[String] and
    (__ \ "description").read[String] and
    (__ \ "createdOn").read[Date] and
    (__ \ "retiredOn").read[Option[Date]]
  )(Project.apply _)

  implicit val writes = (
    (__ \ "id").write[Option[String]] and
    (__ \ "userId").write[String] and
    (__ \ "name").write[String] and
    (__ \ "description").write[String] and
    (__ \ "createdOn").write[Date] and
    (__ \ "retiredOn").write[Option[Date]]
  )(unlift(Project.unapply))

}

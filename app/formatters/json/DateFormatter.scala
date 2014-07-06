package formatters.json

import play.api.libs.json.Json.toJson
import play.api.libs.json._

import java.util.Date
import java.text.SimpleDateFormat

// https://gist.github.com/opensas/2833989
object DateFormatter {

  implicit object JsonDateFormatter extends Format[Option[Date]] {

    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")

    def writes(date: Option[Date]): JsValue = {
      toJson(
        date.map(
          date => dateFormat.format(date)
        ).getOrElse(
            ""
          )
      )
    }

    def reads(j: JsValue): JsResult[Option[Date]] = {
      try {
        JsSuccess(Some(dateFormat.parse(j.as[String])))
      } catch {
        case e: Exception => JsSuccess(None)
      }
    }

  }

}
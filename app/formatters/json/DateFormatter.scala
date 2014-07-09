package formatters.json

import play.api.libs.json.Json.toJson
import play.api.libs.json._

import java.util.Date
import java.text.SimpleDateFormat

// https://gist.github.com/opensas/2833989
object DateFormatter {

  implicit object JsonDateFormatter extends Format[Date] {

    val dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'")

    def writes(date: Date): JsValue = {
      if (date != null)
        toJson(dateFormat.format(date))
      else JsNull
    }

    def reads(j: JsValue): JsResult[Date] = {
      try {
        JsSuccess(dateFormat.parse(j.as[String]))
      } catch {
        case e: Exception => JsSuccess(null)
      }
    }

  }

}
package https

import play.api.Logger
import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.mvc.Action
import play.api.Play.current
import play.api.mvc.Results._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Future
import scala.util.{Failure, Success}

/**
 * Created By: paullawler
 */

object Secured {

  // http://www.playframework.com/documentation/2.3.x/ScalaActionsComposition
  def BasicAuth[A](action: Action[A]) = Action.async(action.parser) { request =>
    request.headers.get("Authorization").map { authorization =>
      decode(authorization) match {
        case Some((u, p)) if authenticateUser(u, p) => action(request)
//          Logger.info(s"Looking up User with $u/$p")
//          authenticateUser(u, p).onComplete {
//            case Success(user) => action(request)
//            case Failure(ex) => Future.successful(Unauthorized("User not authorized"))
//          }
        case None => Future.successful(Unauthorized.withHeaders("WWW-Authenticate" -> """Basic realm="Secured""""))
      }
    } getOrElse {
      Future.successful(Unauthorized("User not authorized"))
    }
  }

  private def decode(authorization: String): Option[(String, String)] = {
    Logger.info(s"About to decode authorization: $authorization")
    authorization.split(" ").drop(1).headOption.flatMap { encoded =>
      new String(org.apache.commons.codec.binary.Base64.decodeBase64(encoded.getBytes)).split(":").toList match {
        case u :: p :: Nil => Some((u, p))
        case _ => None // _ is "everything else" or default
      }
    }
  }

  private def authenticateUser(username: String, password: String): Boolean = {
    //    username == "paullawler" && password == "password"
    val jsonCreds = Json.obj("username" -> username, "password" -> password)
    val result = WS.url("http://localhost:9000/authenticate").post(jsonCreds).map { response =>
//      (response.json \ "username").as[String]
      response.json match {
        case _ => true
      }
    }
    var foo = false
    result.onComplete {
      case Success(r) => foo = true
//      case Failure(ex) => false
    }
    foo
    //    result.map(response => Ok(response.json.toString()))
  }

}

package https

import play.api.Logger
import play.api.mvc.{Action}

import scala.concurrent.Future

import play.api.mvc.Results._

/**
 * Created By: paullawler
 */
object Secured {

  // http://www.playframework.com/documentation/2.3.x/ScalaActionsComposition
  def BasicAuth[A](action: Action[A]) = Action.async(action.parser) { request =>
    request.headers.get("Authorization").map { authorization =>
      decode(authorization) match {
        case Some((u, p)) if validUser(u, p) => Logger.info(s"Looking up User with $u/$p")
          action(request)
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
        case u :: p :: Nil => Some((u,p))
        case _ => None // _ is "everything else" or default
      }
    }
  }

  private def validUser(username: String, password: String): Boolean =
    username == "paullawler" && password == "password"

  //  def Secured[A]()(action: Action[A] => Result) = Action { request =>
  //    request.headers.get("Authorization").flatMap { authorization =>
  //      authorization.split(" ").drop(1).headOption.filter { encoded =>
  //        new String(org.apache.commons.codec.binary.Base64.decodeBase64(encoded.getBytes)).split(":").toList match {
  //          case u :: p :: Nil => true
  //          case _ => false
  //        }
  //      }.map(_ => action(request))
  //    }.getOrElse {
  //      Future.successful(Unauthorized.withHeaders("WWW-Authenticate" -> """Basic realm="Secured""""))
  ////      Unauthorized.withHeaders("WWW-Authenticate" -> """Basic realm="Secured"""")
  //    }
  //  }

}

package https

import controllers.Application._
import play.api.Logger
import play.api.mvc.{Result, Request, ActionBuilder}

import scala.concurrent.Future

/**
 * Created By: paullawler
 */
object BasicAuthAction extends ActionBuilder[Request] {

  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
    Logger.info("Beginning basic authorization")
    request.headers.get("Authorization").map { basicAuth =>
      Logger.info("Pulling apart the auth header...")
//      val (user, pass) = decodeBasicAuth(basicAuth)
//      Logger.info(s"Authenticated $user with $pass")
      Logger.info(decode(basicAuth).get)
      block(request)
    }.getOrElse {
      Future.successful(Unauthorized.withHeaders("WWW-Authenticate" -> """Basic realm="Secured""""))
    }
  }

  private def decodeBasicAuth(auth: String) = {
    val baStr = auth.replaceFirst("Basic ", "")
    val Array(user, pass) = new String(new sun.misc.BASE64Decoder().decodeBuffer(baStr), "UTF-8").split(":")
    (user, pass)
  }

  private def decode(authorization: String): Option[String] = {
    Logger.info(s"Authorization: $authorization")
    authorization.split(" ").drop(1).headOption.filter { encoded =>
      val decoded = new String(org.apache.commons.codec.binary.Base64.decodeBase64(encoded.getBytes))
      Logger.info(s"Decoded authorization: $decoded")
      decoded.split(":").toList match {
        case u :: p :: Nil =>
          Logger.info(s"Decoded user $u with password $p")
          true
        case _ => false
      }
    }
  }

//  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = {
//    Logger.info("Beginning basic authorization")
//    request.headers.get("Authorization").flatMap { authorization =>
//      authorization.split(" ").drop(1).headOption.filter { encoded =>
//        new String(org.apache.commons.codec.binary.Base64.decodeBase64(encoded.getBytes)).split(":").toList match {
//          case u :: p :: Nil => block(request)
//          case _ => Future.successful(Unauthorized.withHeaders("WWW-Authenticate" -> """Basic realm="Secured""""))
//        }
//      }
//    }
//  }

}

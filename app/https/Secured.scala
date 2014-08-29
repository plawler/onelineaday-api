package https

import play.api.Logger
import play.api.libs.json.{JsError, Json}
import play.api.libs.ws.{Response, WSResponse, WS}
import play.api.mvc.{Result, Action}
import play.api.Play.current
import play.api.mvc.Results._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

// for the Duration dsl
import scala.concurrent.duration._
import scala.language.postfixOps

/**
 * Created By: paullawler
 */

case class UserAccount(givenName: String, surName: String, email: String)

object Secured {

  implicit val fmtUserAccount = Json.format[UserAccount]

  // http://www.playframework.com/documentation/2.3.x/ScalaActionsComposition
  def BasicAuth[A](action: Action[A]) = Action.async(action.parser) { request =>
    request.headers.get("Authorization").map { authorization =>
      decode(authorization) match { // decode the Auth header (Base64)
        case Some((username, password)) => // if credentials are presented...
          try {
            val account = authenticate(username, password) // authenticate with creds
            action(request) // use the account at this point...
          } catch {
            case e: Exception => // in the event of an exception
              Logger.info(s"Failed authentication for credentials of $username/$password")
              Future.successful(Unauthorized(e.getMessage))
          }
        case None =>
          Logger.info("No credentials provided in Authorization header")
          Future.successful(Unauthorized.withHeaders("WWW-Authenticate" -> """Basic realm="Secured""""))
      }
    } getOrElse {
      Logger.info("No Authorization header")
      Future.successful(Unauthorized("No authorization credentials were provided"))
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

  private def authenticate(username: String, password: String): UserAccount = {
    val jsonCreds = Json.obj("username" -> username, "password" -> password)
    val account = WS.url("http://localhost:9000/authenticate").post(jsonCreds).map { response =>
      try {
        response.json.validate[UserAccount].get
      } catch {
        case e: Exception => throw new Exception("Failed authentication")
      }
    }
    Await.result(account, 10 seconds) // this bloooooows but I am not sure how to handle without blocking
  }

  /** the following is to help me retain my sanity with this Future stuff */

  private def fooTest() = {
    try {
      authenticate2("foo", "bar").onSuccess {
        case a => println(a)
      }
    } catch {
      case e: Exception => println(e.getMessage)
    }
  }

  private def authenticate2(username: String, password: String): Future[UserAccount] = {
    val jsonCreds = Json.obj("username" -> username, "password" -> password)
    val account = WS.url("http://localhost:9000/authenticate").post(jsonCreds).map { response =>
      val result = response.json.validate[UserAccount]
      if (result.isSuccess) result.get
      else throw new Exception
    }
    account.withFilter(a => a.isInstanceOf[UserAccount])
  }

}

import java.util.concurrent.TimeUnit

import play.api.libs.json.Json
import play.api.libs.ws.WS
import play.api.test.Helpers._
import play.api.test.WithApplication

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import org.specs2.mutable.{ BeforeAfter, Specification }
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._

/**
 * Created By: paullawler
 */
class WireMockSpec extends Specification {

  val userId = "d9220627-05e6-11e4-9180-6fad63942f7f"
  val userAuth = Seq(AUTHORIZATION -> Seq("Basic dGVzdHVzZXJAb25lbGluZWFkYXkubWU6cEFzc3cwcmQ="))

  val Port = 9000
  val Host = "localhost"

  trait StubServer extends BeforeAfter {
    val wireMockServer = new WireMockServer(wireMockConfig().port(Port))

    def before = {
      wireMockServer.start()
      WireMock.configureFor(Host, Port)
    }

    def after = wireMockServer.stop()
  }

  "WireMock" should {
    "stub get request" in new WithApplication() {
      val wireMockServer = new WireMockServer(wireMockConfig().port(Port))

      wireMockServer.start()
      WireMock.configureFor(Host, Port)

      val path = "/authenticate"
      stubFor(post(urlEqualTo(path)).willReturn(aResponse().withStatus(200)))

      val jsonCreds = Json.obj("username" -> "test", "password" -> "password")
      val responseFuture = WS.url(s"http://$Host:$Port$path").post(jsonCreds)
      val response = Await.result(responseFuture, Duration(100, TimeUnit.MILLISECONDS))
      response.status mustEqual 200
    }
  }

}

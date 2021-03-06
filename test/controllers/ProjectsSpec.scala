package controllers

import java.util.Date

import com.fasterxml.uuid.Generators
import models.Project
import org.junit.runner.RunWith
import org.specs2.mutable.{Before, BeforeAfter, Specification}
import org.specs2.runner.JUnitRunner
import org.specs2.specification.{AfterExample, BeforeExample}
import play.api.libs.json.{JsString, Json}
import play.api.mvc.AnyContentAsEmpty

import play.api.test.Helpers._
import play.api.test._

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._


/**
 * Created By: paullawler
 */
@RunWith(classOf[JUnitRunner])
class ProjectsSpec extends Specification with BeforeExample with AfterExample {

  val Port = 9000
  val Host = "localhost"
  val wireMockServer = new WireMockServer(wireMockConfig().port(Port))

  def before = {
    wireMockServer.start()
    WireMock.configureFor(Host, Port)

    val path = "/authenticate"
    stubFor(post(urlEqualTo(path))
      .willReturn(aResponse()
                  .withStatus(200)
                  .withHeader("Content-Type", "application/json")
                  .withBody("{\"givenName\":\"Test\",\"surName\":\"User\",\"email\":\"test@onelineaday.me\"}")))
  }

  def after = wireMockServer.stop()

  val userId = "d9220627-05e6-11e4-9180-6fad63942f7f"
  val userAuth = Seq(AUTHORIZATION -> Seq("Basic dGVzdHVzZXJAb25lbGluZWFkYXkubWU6cEFzc3cwcmQ="))

  "Project controller" should {
    // https://stackoverflow.com/questions/13570125/why-test-method-fails
    "POST a project" in new WithApplication(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val id = Generators.timeBasedGenerator().generate().toString
      val json = Json.toJson(
        Project(Some(id), userId, "Test Project", "Test project description", new Date(), None)
      )(models.Project.writes)

      val request = FakeRequest(POST, "/api/v1/projects", FakeHeaders(userAuth), json)
      val result = controllers.Projects.post(request)

      status(result) must beEqualTo(OK)
      contentType(result) must beSome("application/json") // be(Some(...)) failed for some fucking reason. sigh.
    }

    "GET a project" in new WithApplication(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val id = Generators.timeBasedGenerator().generate().toString
      Project.create(Project(Some(id), userId, "Test the GET", "Test the GET description", new Date(), None))

      val request = FakeRequest(GET, s"/api/v1/projects/$id", FakeHeaders(userAuth), AnyContentAsEmpty) // http://stackoverflow.com/a/22377270
      val result = controllers.Projects.get(id)(request)
      status(result) must beEqualTo(OK)
      contentType(result) must beSome("application/json")
      contentAsString(result) must contain("\"id\":\"" + id + "\"")
    }

    "GET all projects" in new WithApplication(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val request = FakeRequest(GET, "/api/v1/projects", FakeHeaders(userAuth), AnyContentAsEmpty)
      val result = controllers.Projects.all()(request)
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
    }

    "PUT a project" in new WithApplication(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val id = Generators.timeBasedGenerator().generate().toString
      Project.create(Project(Some(id), userId, "Test the PUT", "Test the PUT description", new Date(), None))

      val json = Json.toJson(
        Project(Some(id), userId, "Test the PUT", "Test the PUT description updated", new Date(), None)
      )(models.Project.writes)

      val request = FakeRequest(PUT, s"/api/v1/projects/$id", FakeHeaders(userAuth), json)
      val result = controllers.Projects.put(userId)(request)

      contentType(result) must beSome("application/json")
      contentAsString(result) must contain("\"description\":\"Test the PUT description updated\"")
    }

    "PATCH a project (for retiring)" in new WithApplication(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val id = Generators.timeBasedGenerator().generate().toString
      Project.create(Project(Some(id), userId, "Test the PUT", "Test the PUT description", new Date(), None))

      val json = Json.obj("retiredOn" -> JsString("2014-07-06"))
      val request = FakeRequest(PUT, s"/api/v1/projects/$id", FakeHeaders(userAuth), json)
      val result = controllers.Projects.patch(id)(request)

      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
      contentAsString(result) must contain("\"retiredOn\":\"2014-07-06T12:00:00Z\"")
    }

  }

  /*
    would like to extend this from my specification but it keeps failing with a 401
    i think the reason for this is that BeforeAfter is of type mutable and can only be used in the Example.
    BeforeExample/AfterExample are type Specification and can be used at the Specification level (which in this case is
    what i want to do).
    The naming of these classes is fucking bizarre.
   */
  trait StubAuthServer extends BeforeAfter {
    val Port = 9000
    val Host = "localhost"

    val wireMockServer = new WireMockServer(wireMockConfig().port(Port))

    def before = {
      wireMockServer.start()
      WireMock.configureFor(Host, Port)

      val path = "/authenticate"
      stubFor(post(urlEqualTo(path))
        .willReturn(aResponse()
                    .withStatus(200)
                    .withHeader("Content-Type", "application/json")
                    .withBody("{\"givenName\":\"Test\",\"surName\":\"User\",\"email\":\"testuser@onelineaday.me\"}")))
    }

    def after = wireMockServer.stop()
  }


}

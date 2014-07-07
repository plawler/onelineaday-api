package controllers

import java.util.Date

import models.Project
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.libs.json.{JsString, Json}

import play.api.test.Helpers._
import play.api.test.{Helpers, FakeHeaders, FakeRequest}


/**
 * Created By: paullawler
 */
@RunWith(classOf[JUnitRunner])
class ProjectsSpec extends Specification {

  val id = "d9227b5f-05e6-11e4-9180-cd98919f6869"
  val userId = "d9220627-05e6-11e4-9180-6fad63942f7f"

  "Project controller" should {

//    https://stackoverflow.com/questions/13570125/why-test-method-fails
    "POST a project" in {
      val json = Json.toJson(
        Project(Some(id), userId, "Test Project", "Test project description", new Date(), None)
      )(models.Project.writes)

      val request = FakeRequest(POST, "/api/v1/projects", FakeHeaders(), json)
      val result = controllers.Projects.post(request)

      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json") // be(Some(...)) failed for some fucking reason. sigh.
      contentAsString(result) must contain("\"id\":\"d9227b5f-05e6-11e4-9180-cd98919f6869\"")
    }

    "GET a project" in {
      val request = FakeRequest(GET, s"/api/v1/projects/$id")
      val result = controllers.Projects.get(id)(request)
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json") // be(Some(...)) failed for some fucking reason. sigh.
      contentAsString(result) must contain("\"id\":\"d9227b5f-05e6-11e4-9180-cd98919f6869\"")
    }

    "PUT a project" in {
      val json = Json.toJson(
        Project(Some(id), userId, "Test Project", "Test project description updated", new Date(), None)
      )(models.Project.writes)

      val request = FakeRequest(PUT, "/api/v1/projects/12345678", FakeHeaders(), json)
      val result = controllers.Projects.put(userId)(request)

      contentType(result) must beSome("application/json")
      contentAsString(result) must contain("\"description\":\"Test project description updated\"")
    }

    "PATCH a project (for retiring)" in {
      val json = Json.obj("retiredOn" -> JsString("2014-07-06"))
      val request = FakeRequest(PUT, "/api/v1/projects/12345678", FakeHeaders(), json)
      val result = controllers.Projects.patch(id)(request)
      status(result) must equalTo(OK)
      contentType(result) must beSome("application/json")
      contentAsString(result) must contain("\"retiredOn\":\"2014-07-06T12:00:00Z\"")
    }

  }

}

package models

import java.util.Date

import com.fasterxml.uuid.Generators
import org.specs2.mutable.Specification
import play.api.Logger
import play.api.test.Helpers._
import play.api.test.{FakeApplication, WithApplication}

/**
 * Created By: paullawler
 */
class ProjectSpec extends Specification {

//  val id = "d9227b5f-05e6-11e4-9180-cd98919f6869"
  val userId = "d9220627-05e6-11e4-9180-6fad63942f7f"


  "Project" should {

    "create a project" in new WithApplication(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val projectId = Generators.timeBasedGenerator().generate().toString
      val result = Project.create(Project(Some(projectId), userId, "Test Project", "Test project description", new Date(), None))
      result mustEqual 1
    }
  }

}

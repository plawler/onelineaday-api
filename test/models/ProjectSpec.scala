package models

import java.util.Date

import com.fasterxml.uuid.Generators
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.Logger
import play.api.test.Helpers._
import play.api.test.{FakeApplication, WithApplication}

/**
 * Created By: paullawler
 */
@RunWith(classOf[JUnitRunner])
class ProjectSpec extends Specification {

//  val id = "d9227b5f-05e6-11e4-9180-cd98919f6869"
  val userId = "d9220627-05e6-11e4-9180-6fad63942f7f"


  "Project" should {

    "create, find and update a project" in new WithApplication(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      val projectId = Generators.timeBasedGenerator().generate().toString
      val result = Project.create(Project(Some(projectId), userId, "Test Project", "Test project description", new Date(), None))
      result mustEqual 1

      val project = Project.find(projectId).get
      project mustNotEqual None
      project.name mustEqual("Test Project")

      val modifiedProject = Project(project.id, project.userId, project.name + " Updated", project.description, project.createdOn, None)
      val update = Project.update(modifiedProject)
      update mustEqual 1
      val p = Project.find(projectId).get
      p.name mustEqual "Test Project Updated"
    }

    "find all projects" in new WithApplication(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
      for (i <- 1 to 10) {
        val id = Generators.timeBasedGenerator().generate().toString
        Project.create(Project(Some(id), userId, "Test Project", "Test project description", new Date(), None))
      }
      Project.findAll(userId).size mustEqual 10
    }

  }

}

import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner
import play.api.Logger

import com.fasterxml.uuid.Generators
import play.api.test.{FakeApplication, WithApplication}

/**
 * Created By: paullawler
 */

@RunWith(classOf[JUnitRunner])
class UUIDGeneratorSpec extends Specification {

  val logger = Logger(this.getClass)

  "Java UUID Generator (JUG)" should {

    // http://stackoverflow.com/a/21488180
    "generate sequential UUIDs" in new WithApplication(FakeApplication(additionalConfiguration = Map("logger.root" -> "INFO"))) {
      for (i <- 1 to 10) {
        logger.info(s"UUID #$i:" + Generators.timeBasedGenerator().generate().toString)
      }
      true
    }

  }

}

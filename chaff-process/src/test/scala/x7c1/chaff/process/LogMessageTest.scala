package x7c1.chaff.process

import org.scalatest.{FreeSpecLike, Matchers}
import x7c1.chaff.process.LogMessage.Info
import x7c1.chaff.reader.Reader

class LogMessageTest extends FreeSpecLike with Matchers {

  "LogMessage" - {
    ".toReader" - {
      "should return R[X, Unit]" in {
        val reader = Info("foo").toReader[HasProcessLogger, Reader]
        val logger = new BufferedLogger
        reader run logger
        logger.lines shouldBe Seq("foo")
      }
    }

  }

}

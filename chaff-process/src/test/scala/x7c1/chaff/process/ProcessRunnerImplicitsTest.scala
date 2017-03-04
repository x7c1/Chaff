package x7c1.chaff.process

import org.scalatest.{FreeSpecLike, Matchers}
import x7c1.chaff.process.LogMessage.{Error, Info}

import scala.util.{Left, Right}

class ProcessRunnerImplicitsTest extends FreeSpecLike with Matchers {

  implicit val fromRight = HasLogMessage {
    exitCode: Int => Info(s"[done] $exitCode")
  }
  implicit val fromLeft = HasLogMessage {
    error: String => Error(s"[failed] $error")
  }

  private def setup1(i: Option[Int]) = i match {
    case Some(x) => Right(x)
    case None => Left("error1")
  }

  private def setup2(i: Option[Int]) = i match {
    case Some(x) => Right(x)
    case None => Left("error2")
  }

  "() => Either[L: HasLogMessage, ProcessRunner]" - {
    ".asLoggerApplied" - {
      "when [ProcessRunner] is given" - {
        val target = () => for {
          r1 <- setup1(Some(100)).right
          r2 <- setup2(Some(23)).right
        } yield {
          ProcessRunner(Seq("echo", s"${r1 + r2}"))
        }
        "should output Right message " - {
          val logger = new BufferedLogger
          target.asLoggerApplied run logger
          logger.lines should contain("[run] echo 123")
          logger.lines should contain("[done] 0")
        }
        "should output command to logger" - {
          val logger = new BufferedLogger
          target.asLoggerApplied run logger
          logger.lines should contain("123")
        }
      }
      "when [L: HasLogMessage] is given" - {
        val target = () => for {
          r1 <- setup1(None).right
          r2 <- setup2(Some(23)).right
        } yield {
          ProcessRunner(Seq("echo", s"${r1 + r2}"))
        }
        "should output Left message" - {
          val logger = new BufferedLogger
          target.asLoggerApplied run logger
          logger.lines should contain("[failed] error1")
        }
      }
    }
  }

}

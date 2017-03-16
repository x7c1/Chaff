package x7c1.chaff.process

import org.scalatest.{FreeSpecLike, Matchers}
import x7c1.chaff.process.LogMessage.{Error, Info}
import x7c1.chaff.reader.Reader

import scala.util.{Left, Right}

class LogMessageReadersTest extends FreeSpecLike with Matchers {

  "Seq[R[X, LogMessage]]" - {
    ".uniteSequentially can generate R[X, Unit] that" - {

      val toMessage: Either[ProcessError, Seq[String]] => LogMessage = {
        case Right(lines) => Info(lines mkString "\n")
        case Left(error) => Error(error.cause.getMessage)
      }
      val target: Seq[ProcessRunner] => Seq[Reader[HasProcessLogger, LogMessage]] = {
        _ map (_.lines) map (_ map toMessage)
      }
      "should run readers until Error found" in {
        val runners = Seq(
          ProcessRunner(Seq("ls")),
          ProcessRunner(Seq("foobar"))
        )
        val logger = new BufferedLogger
        target(runners).uniteSequentially run logger

        logger.lines should contain inOrder("[run] ls", "[run] foobar")
      }
      "should stop execution when Error found" in {
        val runners = Seq(
          ProcessRunner(Seq("foobar")),
          ProcessRunner(Seq("ls"))
        )
        val logger = new BufferedLogger
        target(runners).uniteSequentially run logger

        logger.lines should contain("[run] foobar")
        logger.lines shouldNot contain("[run] ls")
      }
    }
  }
}

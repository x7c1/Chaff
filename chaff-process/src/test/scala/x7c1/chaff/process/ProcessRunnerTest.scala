package x7c1.chaff.process

import java.io.IOException

import org.scalatest.{FlatSpecLike, Matchers}
import sbt.ProcessLogger

import scala.collection.mutable.ArrayBuffer
import scala.util.{Left, Right}

class ProcessRunnerTest extends FlatSpecLike with Matchers {

  ".lines" can "return Seq[String] if succeeded" in {
    val logger = new BufferedLogger
    val runner = ProcessRunner(Seq("ls"))

    runner.lines run logger match {
      case Left(error) =>
        fail(error.cause)
      case Right(lines) =>
        lines should contain("LICENSE")
        lines should contain("README.md")
    }
    logger.lines should contain("[run] ls")
  }

  ".lines" should "return ProcessError to non-existent command" in {
    val logger = new BufferedLogger
    val runner = ProcessRunner(Seq("non-existent-command"))

    runner.lines run logger match {
      case Left(error) =>
        error.cause.getMessage should include("error=2")
      case Right(lines) =>
        fail(s"unexpected output: $lines")
    }
    logger.lines should contain("[run] non-existent-command")
  }

  ".lines" should "return ProcessError if not succeeded" in {
    val logger = new BufferedLogger
    val runner = ProcessRunner(Seq("ls", "foobar"))

    runner.lines run logger match {
      case Left(error) =>
        error.cause.getMessage should include("Nonzero exit code: 1")
      case Right(lines) =>
        fail(s"unexpected output: $lines")
    }
    logger.lines should contain("[run] ls foobar")
  }

  ".exitCode" should "return zero if succeeded" in {
    val logger = new BufferedLogger
    val runner = ProcessRunner(Seq("ls"))

    runner.exitCode run logger should be(0)

    logger.lines should contain("[run] ls")
    logger.lines should contain("LICENSE")
    logger.lines should contain("README.md")
  }

  ".exitCode" should "throw IOException to non-existent command" in {
    val logger = new BufferedLogger
    val runner = ProcessRunner(Seq("non-existent-command"))

    a[IOException] shouldBe thrownBy {
      runner.exitCode run logger
    }
    logger.lines should contain("[run] non-existent-command")
  }

  ".exitCode" should "return non-zero if not succeeded" in {
    val logger = new BufferedLogger
    val runner = ProcessRunner(Seq("ls", "foobar"))

    runner.exitCode run logger shouldNot be(0)
    logger.lines should contain("[run] ls foobar")
  }
}

class BufferedLogger extends ProcessLogger {
  val lines: ArrayBuffer[String] = new ArrayBuffer()

  override def info(s: => String): Unit = lines += s

  override def error(s: => String): Unit = lines += s

  override def buffer[T](f: => T): T = f
}
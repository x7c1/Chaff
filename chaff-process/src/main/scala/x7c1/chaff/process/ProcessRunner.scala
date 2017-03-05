package x7c1.chaff.process

import sbt.Process
import x7c1.chaff.process.HasProcessLogger.LogReader

import scala.util.{Failure, Left, Right, Success, Try}


case class ProcessRunner(command: Seq[String]) {

  import x7c1.chaff.process.ProcessRunner.To

  private def builder = Process(command)

  def explain: To[Unit] = {
    val line = command mkString " "
    LogReader(_ info s"[run] $line")
  }

  def exitCode: To[Int] = {
    explain append raw.exitCode
  }

  def lines: To[Either[ProcessError, Seq[String]]] = {
    explain append raw.lines
  }

  object raw {

    def exitCode: To[Int] = LogReader { logger =>
      builder !< logger
    }

    def lines: To[Either[ProcessError, Seq[String]]] =
      LogReader { logger =>
        Try {
          builder lines_! logger
        } match {
          case Success(lines) => Right(lines.toIndexedSeq)
          case Failure(exception) => Left(ProcessError(exception))
        }
      }
  }

}

object ProcessRunner {

  import x7c1.chaff.reader.Reader

  type To[A] = Reader[HasProcessLogger, A]

  implicit class EitherRightReader[L: HasLogMessage](
    either: () => Either[L, ProcessRunner]) {

    def toLogReader: To[Either[L, Int]] = {
      Reader((_: HasProcessLogger) => either()) flatMap {
        case Right(right) => right.exitCode map Right.apply
        case Left(left) => Reader(_ => Left(left))
      }
    }

    def asLoggerApplied(implicit x: HasLogMessage[Int]): To[Unit] = {
      toLogReader.asLoggerApplied
    }
  }

}

case class ProcessError(cause: Throwable)

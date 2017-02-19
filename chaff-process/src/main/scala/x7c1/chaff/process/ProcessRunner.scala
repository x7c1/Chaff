package x7c1.chaff.process

import sbt.Process
import x7c1.chaff.process.HasProcessLogger.LogReader
import x7c1.chaff.reader.Reader

import scala.util.{Left, Right}

case class ProcessRunner(command: Seq[String]) {

  private def builder = Process(command)

  private def raw = command mkString " "

  def reader: Reader[HasProcessLogger, Int] = LogReader { logger =>
    logger info s"[run] $raw"
    builder !< logger
  }
}

object ProcessRunner {

  implicit class EitherRightReader[L: HasLogMessage](
    either: () => Either[L, ProcessRunner])(implicit x: HasLogMessage[Int]) {

    def toLogReader: Reader[HasProcessLogger, Either[L, Int]] = {
      Reader((_: HasProcessLogger) => either()) flatMap {
        case Right(right) => right.reader map Right.apply
        case Left(left) => Reader(_ => Left(left))
      }
    }

    def asLoggerApplied: Reader[HasProcessLogger, Unit] = {
      toLogReader.asLoggerApplied
    }
  }

}

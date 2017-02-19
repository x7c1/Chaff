package x7c1.chaff.process

import sbt.{Logger, ProcessLogger}
import x7c1.chaff.reader.Reader

trait HasProcessLogger {
  def logger: ProcessLogger
}

object HasProcessLogger {

  implicit class fromLogger(x: Logger) extends HasProcessLogger {
    override def logger: ProcessLogger = Logger.log2PLog(x)
  }

  implicit class fromProcessLogger(x: ProcessLogger) extends HasProcessLogger {
    override def logger: ProcessLogger = x
  }

  implicit class RichEitherReader[A <: HasProcessLogger, L: HasLogMessage, R: HasLogMessage](
    reader: Reader[A, Either[L, R]]) {

    def asLoggerApplied: Reader[A, Unit] = {
      reader map {
        case Right(right) => implicitly[HasLogMessage[R]] messageOf right
        case Left(left) => implicitly[HasLogMessage[L]] messageOf left
      } flatMap (_.toReader)
    }
  }

  def LogReader[A](f: ProcessLogger => A): Reader[HasProcessLogger, A] =
    Reader { context =>
      f(context.logger)
    }
}

package x7c1.chaff.process

import sbt.{Logger, ProcessLogger}
import x7c1.chaff.core.{Apply, Determined, Monad}
import x7c1.chaff.reader.{BaseReader, Reader}

import scala.language.{higherKinds, reflectiveCalls}

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

  implicit class RichEitherReader[
  X <: HasProcessLogger,
  R[X0, T] <: BaseReader[X0, T],
  LEFT: HasLogMessage,
  RIGHT: HasLogMessage](reader: R[X, Either[LEFT, RIGHT]])
    (implicit
      monad: Monad[R[X, ?]],
      apply: Apply[R[X, ?]],
      determined: Determined[R[X, ?], X]) {

    def asLoggerApplied: R[X, Unit] = {
      val r: R[X, LogMessage] = monad.map(reader) {
        case Right(right) => implicitly[HasLogMessage[RIGHT]] messageOf right
        case Left(left) => implicitly[HasLogMessage[LEFT]] messageOf left
      }
      monad.flatMap(r)(_.toReader[X, R[X, ?]])
    }
  }

  /*
  type To[A] = Reader[HasProcessLogger, A]
  def toLogReader: To[Either[L, Int]] = ???

  // compiler cannot infer type [X] here by RichEitherReader0 below
  toLogReader.asLoggerApplied

  implicit class RichEitherReader0[
  X <: HasProcessLogger,
  R[_] <: BaseReader[_, _] : Monad : Apply : Determined[?[_], X],
  LEFT: HasLogMessage,
  RIGHT: HasLogMessage](reader: R[Either[LEFT, RIGHT]]) {

    def asLoggerApplied: R[Unit] = {
      val monad = implicitly[Monad[R]]
      val r: R[LogMessage] = monad.map(reader) {
        case Right(right) => implicitly[HasLogMessage[RIGHT]] messageOf right
        case Left(left) => implicitly[HasLogMessage[LEFT]] messageOf left
      }
      monad.flatMap(r)(_.toReader[X, R])
    }
  }
  */

  def LogReader[A](f: ProcessLogger => A): Reader[HasProcessLogger, A] =
    Reader { context =>
      f(context.logger)
    }
}

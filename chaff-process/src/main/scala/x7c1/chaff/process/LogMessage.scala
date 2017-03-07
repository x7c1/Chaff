package x7c1.chaff.process

import x7c1.chaff.core.{Apply, Determined, Monad, Pure}

import scala.language.{higherKinds, reflectiveCalls}

sealed trait LogMessage {
  def messages: Seq[String]
}

object LogMessage {

  case class Error(messages: String*) extends LogMessage

  case class Info(messages: String*) extends LogMessage

  implicit class ReaderLike(message: LogMessage) {

    def toReader[
    X <: HasProcessLogger,
    R[X0, A0]
    ](implicit
      pure: Pure[R[X, ?]],
      apply: Apply[R[X, ?]],
      determined: Determined[R[X, ?], X]): R[X, Unit] = {

      val rf: R[X, X => Unit] = pure pure { context =>
        val logger = context.logger
        message match {
          case _: Error => message.messages foreach (logger.error(_))
          case _: Info => message.messages foreach (logger.info(_))
        }
      }
      val rx: R[X, X] = determined.applied
      apply(rf)(rx)
    }
  }

  implicit class LogMessageReaders[
  X <: HasProcessLogger,
  R[X0, A0]](readers: Seq[R[X, LogMessage]])
    (implicit
      monad: Monad[R[X, ?]],
      apply: Apply[R[X, ?]],
      determined: Determined[R[X, ?], X]) {

    def uniteSequentially: R[X, Unit] = {
      def loop(xs: Seq[R[X, LogMessage]]): R[X, Unit] = xs match {
        case head +: Nil =>
          monad.flatMap(head)(_.toReader[X, R])
        case head +: tail => monad.flatMap(head) {
          case m: Info => monad.flatMap(m.toReader[X, R])(_ => loop(tail))
          case m: Error => m.toReader[X, R]
        }
      }

      loop(readers)
    }
  }

}

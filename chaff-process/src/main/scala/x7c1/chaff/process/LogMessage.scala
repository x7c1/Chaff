package x7c1.chaff.process

import x7c1.chaff.reader.Reader


sealed trait LogMessage {
  def messages: Seq[String]
}

object LogMessage {

  case class Error(messages: String*) extends LogMessage

  case class Info(messages: String*) extends LogMessage

  case class Multiple(raw: Seq[LogMessage]) extends LogMessage {
    override def messages: Seq[String] = raw.flatMap(_.messages)
  }

  implicit class ReaderLike(message: LogMessage) {
    def toReader[A <: HasProcessLogger]: Reader[A, Unit] = Reader { context =>
      val logger = context.logger
      message match {
        case _: Error => message.messages foreach (logger.error(_))
        case _: Info => message.messages foreach (logger.info(_))
        case Multiple(raw) => (raw map (_.toReader[A])).uniteAll run context
      }
    }
  }

}

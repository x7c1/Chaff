package x7c1.chaff.process

trait HasLogMessage[A] {
  def messageOf(x: A): LogMessage
}

object HasLogMessage {
  def apply[A](f: A => LogMessage): HasLogMessage[A] =
    new HasLogMessage[A] {
      override def messageOf(x: A): LogMessage = f(x)
    }
}

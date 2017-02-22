package x7c1.chaff.reader

object ReaderRunner {

  def run[X, A](x: X, reader: Reader[X, A]): A = {
    val stack = collection.mutable.Stack[Any](reader)
    var a: Option[A] = None
    while (stack.nonEmpty) {
      val current = stack.pop()
      current match {
        case Reader2(fa, f) =>
          stack push f
          stack push fa
        case fa: Reader[X, _] =>
          stack push fa.run(x)
        case value if stack.nonEmpty =>
          val f = stack.pop().asInstanceOf[Any => Any]
          stack push f(value)
        case value =>
          a = Some(value.asInstanceOf[A])
      }
    }
    a.get
  }

  def traverse(node: ReaderNodeSample)(f: String => Unit) = {
    val stack = collection.mutable.Stack[Any](node)
    val map = collection.mutable.Map[Any, Boolean]()
    val notVisited = !map.getOrElse(_: Any, false)

    while (stack.nonEmpty) {
      val current = stack.head
      current match {
        case ReaderNodeSample(_, Some(left), _) if notVisited(left) =>
          stack push left
        case ReaderNodeSample(_, _, Some(right)) if notVisited(right) =>
          stack push right
        case _ =>
          val x = current match {
            case Some(x: ReaderNodeSample) => x
            case x: ReaderNodeSample => x
            case _ =>
              throw new IllegalStateException(s"unknown type: $current")
          }
          f(x.label)
          map(current) = true
          stack.pop()
      }
    }
  }

}

case class ReaderNodeSample(
  label: String,
  left: Option[ReaderNodeSample] = None,
  right: Option[ReaderNodeSample] = None
)

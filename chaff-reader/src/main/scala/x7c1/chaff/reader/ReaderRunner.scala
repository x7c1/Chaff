package x7c1.chaff.reader

object ReaderRunner {

  def traverse(node: ReaderNodeSample)(f: String => Unit) = {
    val stack = collection.mutable.Stack[Any](node)
    val map = collection.mutable.Map[Any, Boolean]()
    val notVisited = ! map.getOrElse(_: Any, false)

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

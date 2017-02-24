package x7c1.chaff.reader

import scala.collection.immutable

object ReaderRunner {

  def run[X, A](reader: BaseReader[X, A]): X => A = x => {

    @scala.annotation.tailrec
    def loop(stack: List[Any]): A = {
      stack match {
        case (head: FlatMapped[_, _, _]) :: tail =>
          loop(head.fa :: head.f :: tail)
        case (fa: BaseReader[X, _]) :: tail =>
          loop(fa.run(x) :: tail)
        case head :: f :: tail =>
          loop(f.asInstanceOf[Any => Any](head) :: tail)
        case head :: Nil =>
          head.asInstanceOf[A]
      }
    }

    loop(List(reader))
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

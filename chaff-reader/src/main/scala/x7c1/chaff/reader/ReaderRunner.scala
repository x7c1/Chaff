package x7c1.chaff.reader

import scala.collection.immutable

object ReaderRunner {

  def run3[X, A](x: X, reader: Reader[X, A]): A = {

    @scala.annotation.tailrec
    def loop(stack: List[Any]): A = {
      stack match {
        case Reader2(fa, f) :: tail =>
          loop(fa :: f :: tail)
        case (fa: Reader[X, _]) :: tail =>
          loop(fa.run(x) :: tail)
        case head :: f :: tail =>
          loop(f.asInstanceOf[Any => Any](head) :: tail)
        case head :: Nil =>
          head.asInstanceOf[A]
      }
    }
    loop(List(reader))
  }

  def run2[X, A](x: X, reader: Reader[X, A]): A = {

    @scala.annotation.tailrec
    def loop(stack: immutable.Stack[Any]): A = {
      stack.pop2 match {
        case (Reader2(fa, f), next) =>
          loop(next.push(f, fa))
        case (fa: Reader[X, _], next) =>
          loop(next push fa.run(x))
        case (value, next) if next.nonEmpty =>
          val (f, next2) = next.pop2
          loop(next2 push f.asInstanceOf[Any => Any](value))
        case (value, _) =>
          value.asInstanceOf[A]
      }
    }
    loop(immutable.Stack(reader))
  }

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

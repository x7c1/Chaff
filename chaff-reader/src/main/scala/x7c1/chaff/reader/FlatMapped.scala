package x7c1.chaff.reader


trait FlatMapped[X, A, B] extends BaseReader[X, B] {

  def fa: Type[A]

  def f: A => Type[B]

  override def run: X => B = {
    FlatMapped extract this
  }
}

object FlatMapped {

  def extract[X, A](reader: BaseReader[X, A]): X => A = x => {

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
}

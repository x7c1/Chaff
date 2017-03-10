package x7c1.chaff.reader


trait Reader[X, A] extends BaseReader[X, A] {

  override type This[T] = Reader[X, T]

  override def underlying = this
}

object Reader extends BaseProvider[Reader] {

  override def apply[X, A](f: X => A): Reader[X, A] = {
    new Reader[X, A] {
      override def run = f
    }
  }

  override def flatMap[X, A, B](fa0: Reader[X, A])(f0: A => Reader[X, B]): Reader[X, B] = {
    new Reader[X, B] with FlatMapped[X, A, B] {

      override def fa = fa0

      override def f = f0
    }
  }

}

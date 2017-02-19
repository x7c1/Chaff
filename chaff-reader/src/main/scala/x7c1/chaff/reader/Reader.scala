package x7c1.chaff.reader

trait Reader[X, A] extends BaseReader[X, A] {

  override type Type[T] = Reader[X, T]

  override def underlying = this
}

object Reader extends BaseProvider[Reader] {

  override def apply[X, A](f: X => A): Reader[X, A] = {
    new Reader[X, A] {
      override def run: X => A = f
    }
  }
}

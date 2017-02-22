package x7c1.chaff.reader

trait Reader[X, A] extends BaseReader[X, A] {

  override type Type[T] = Reader[X, T]

  override def underlying = this
}

case class Reader2[X, ORIGINAL, CURRENT](
  fa: Reader[X, ORIGINAL],
  f: ORIGINAL => Reader[X, CURRENT]) extends Reader[X, CURRENT] {

  override def run: X => CURRENT = x => {
    ReaderRunner.run(x, this)
  }
}

object Reader extends BaseProvider[Reader] {

  override def apply[X, A](f: X => A): Reader[X, A] = {
    new Reader[X, A] {
      override def run: X => A = f
    }
  }

  override def apply2[X, A, B](fa: Reader[X, A])(f: A => Reader[X, B]): Reader[X, B] = {
    Reader2(fa, f)
  }

}

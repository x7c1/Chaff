package x7c1.chaff.reader

import scala.language.higherKinds

trait Monad[F[_]] extends HasUnit[F] with HasFlatMap[F]

object Monad {

  class ForUnits[B[_] : Monad](readers: Seq[B[Unit]]) {
    def uniteAll: B[Unit] = {
      val nop = implicitly[Monad[B]] unit {}
      readers.foldLeft(nop) {
        (a, b) => HasFlatMap.append(a, b)
      }
    }
  }

}

trait HasUnit[F[_]] {
  def unit[A](a: A): F[A]
}

trait HasFlatMap[F[_]] {
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
}

object HasFlatMap {

  def append[F[_] : HasFlatMap](a: F[Unit], b: F[Unit]): F[Unit] = {
    val i = implicitly[HasFlatMap[F]]
    i.flatMap(a)(_ => b)
  }

  class ForUnit[B[_] : HasFlatMap](reader: B[Unit]) {
    def append(next: B[Unit]): B[Unit] = {
      HasFlatMap.append(reader, next)
    }
  }

}

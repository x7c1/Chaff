package x7c1.chaff.core

import scala.language.higherKinds


trait FlatMap[F[_]] {
  def flatMap[A, B](fa: F[A])(f: A => F[B]): F[B]
}

object FlatMap {

  def append[F[_] : FlatMap, A](a: F[Unit], b: F[A]): F[A] = {
    val i = implicitly[FlatMap[F]]
    i.flatMap(a)(_ => b)
  }

  class ForUnit[F[_] : FlatMap](reader: F[Unit]) {
    def append[A](next: F[A]): F[A] = {
      FlatMap.append(reader, next)
    }
  }

}

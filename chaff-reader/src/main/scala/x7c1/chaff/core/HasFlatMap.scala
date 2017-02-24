package x7c1.chaff.core

import scala.language.higherKinds


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

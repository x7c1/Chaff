package x7c1.chaff.core

import scala.language.higherKinds

trait Monad[F[_]] extends Pure[F] with FlatMap[F]

object Monad {

  class ForUnits[B[_] : Monad](readers: Seq[B[Unit]]) {
    def uniteAll: B[Unit] = {
      val nop = implicitly[Monad[B]] pure {}
      readers.foldLeft(nop) {
        (a, b) => FlatMap.append(a, b)
      }
    }
  }

}

trait Pure[F[_]] {
  def pure[A](a: A): F[A]
}

package x7c1.chaff.core

import scala.language.higherKinds

trait Monad[F[_]] extends HasPure[F] with HasFlatMap[F]

object Monad {

  class ForUnits[B[_] : Monad](readers: Seq[B[Unit]]) {
    def uniteAll: B[Unit] = {
      val nop = implicitly[Monad[B]] pure {}
      readers.foldLeft(nop) {
        (a, b) => HasFlatMap.append(a, b)
      }
    }
  }

}

trait HasPure[F[_]] {
  def pure[A](a: A): F[A]
}

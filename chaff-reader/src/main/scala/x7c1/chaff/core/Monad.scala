package x7c1.chaff.core

import scala.language.higherKinds

trait Monad[F[_]]
  extends Pure[F]
    with Functor[F]
    with FlatMap[F]
    with Apply[F]

object Monad {

  class ForUnits[F[_] : Monad](readers: Seq[F[Unit]]) {
    def uniteAll: F[Unit] = {
      val nop = implicitly[Monad[F]] pure {}
      readers.foldLeft(nop) {
        (a, b) => FlatMap.append(a, b)
      }
    }
  }

  class Factory[F[_]] {
    def from[G[?[_]] : MonadInducible](x: G[F]): Monad[F] = {
      implicitly[MonadInducible[G]] induceFrom x
    }
  }

  def create[F[_]]: Factory[F] = new Factory
}

trait Functor[F[_]] {
  def map[A, B](fa: F[A])(f: A => B): F[B]
}

trait Pure[F[_]] {
  def pure[A](a: => A): F[A]
}

trait Apply[F[_]] {
  def apply[A, B](f: F[A => B])(a: F[A]): F[B]
}

trait Determined[F[_], A] {
  def applied: F[A]
}

package x7c1.chaff.core

import scala.language.higherKinds

trait Monad[F[_]] extends Pure[F] with FlatMap[F] with Functor[F] {

  override def map[A, B](fa: F[A])(f: A => B): F[B] = {
    flatMap(fa)(a => pure(f(a)))
  }
}

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

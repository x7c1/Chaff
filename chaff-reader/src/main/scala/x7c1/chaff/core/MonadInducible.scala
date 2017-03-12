package x7c1.chaff.core

import scala.language.{higherKinds, implicitConversions}

trait MonadInducible[G[?[_]]] {
  def induceFrom[F[_]](x: G[F]): Monad[F]
}

object MonadInducible {

  type PureFlatMap[X[_]] = Pure[X] with FlatMap[X]

  implicit object pureFlatMap extends MonadInducible[PureFlatMap] {
    override def induceFrom[F[_]](x: PureFlatMap[F]) =

      new Monad[F] with FunctorImpl[F] with ApplyImpl[F] {

        override def pure[A](a: => A) = x.pure(a)

        override def flatMap[A, B](fa: F[A])(f: A => F[B]) = x.flatMap(fa)(f)
      }
  }

}

trait FunctorImpl[F[_]] {
  self: Functor[F] with Pure[F] with FlatMap[F] =>

  override def map[A, B](fa: F[A])(f: A => B): F[B] = {
    flatMap(fa) { a =>
      pure(f(a))
    }
  }
}

trait ApplyImpl[F[_]] {
  this: Apply[F] with Functor[F] with FlatMap[F] =>

  override def apply[A, B](fab: F[A => B])(fa: F[A]): F[B] = {
    flatMap(fab)(map(fa))
  }
}

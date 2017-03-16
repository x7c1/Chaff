package x7c1.chaff.reader

import x7c1.chaff.core.MonadInducible.PureFlatMap
import x7c1.chaff.core.{Determined, FlatMap, Monad, Pure}

import scala.language.{higherKinds, reflectiveCalls}


trait BaseReader[X, A] {

  type This[T] <: BaseReader[X, T]

  def run: X => A

  def underlying: This[A]

  def map[B](f: A => B)(implicit m: Monad[This]): This[B] = {
    m.flatMap(underlying)(a => m pure f(a))
  }

  def flatMap[B](f: A => This[B])(implicit m: Monad[This]): This[B] = {
    m.flatMap(underlying)(f)
  }

}

trait BaseProvider[R[X, A] <: BaseReader[X, A]] {

  def apply[X, A](f: X => A): R[X, A]

  def flatMap[X, A, B](fa: R[X, A])(f: A => R[X, B]): R[X, B]

  def empty[X]: R[X, Unit] = apply(_ => {})

  implicit class RichUnitReader[A](reader: R[A, Unit])
    extends FlatMap.ForUnit[R[A, ?]](reader)

  implicit class RichUnitReaders[A](readers: Seq[R[A, Unit]])
    extends Monad.ForUnits[R[A, ?]](readers)

  implicit def determined[A]: Determined[R[A, ?], A] =
    new Determined[R[A, ?], A] {
      override def applied = BaseProvider.this.apply[A, A](identity)
    }

  implicit def monad[X]: Monad[R[X, ?]] = Monad.create[R[X, ?]].from[PureFlatMap](
    new Pure[R[X, ?]] with FlatMap[R[X, ?]] {
      override def pure[A](a: => A) = {
        BaseProvider.this.apply(_ => a)
      }

      override def flatMap[A, B](fa: R[X, A])(f: A => R[X, B]) = {
        BaseProvider.this.flatMap(fa)(f)
      }
    })

}


package x7c1.chaff.reader

import x7c1.chaff.core.{HasFlatMap, Monad}

import scala.language.{higherKinds, reflectiveCalls}


trait BaseReader[X, A] {

  type Type[T] <: BaseReader[X, T]

  def run: X => A

  def underlying: Type[A]

  def map[B](f: A => B)(implicit m: Monad[Type]): Type[B] = {
    m.flatMap(underlying)(a => m pure f(a))
  }

  def flatMap[B](f: A => Type[B])(implicit m: Monad[Type]): Type[B] = {
    m.flatMap(underlying)(f)
  }

}

trait BaseProvider[R[X, A] <: BaseReader[X, A]] {

  def apply[X, A](f: X => A): R[X, A]

  def flatMap[X, A, B](fa: R[X, A])(f: A => R[X, B]): R[X, B]

  implicit class RichUnitReader[A](reader: R[A, Unit])
    extends HasFlatMap.ForUnit[({type L[T] = R[A, T]})#L](reader)

  implicit class RichUnitReaders[A](readers: Seq[R[A, Unit]])
    extends Monad.ForUnits[({type L[T] = R[A, T]})#L](readers)

  implicit def monad[X]: Monad[({type L[T] = R[X, T]})#L] = new MonadImpl

  private class MonadImpl[X] extends Monad[({type L[T] = R[X, T]})#L] {

    override def pure[A](a: A) = {
      BaseProvider.this.apply(_ => a)
    }

    override def flatMap[A, B](fa: R[X, A])(f: A => R[X, B]) = {
      BaseProvider.this.flatMap(fa)(f)
    }
  }

}

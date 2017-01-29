// See LICENSE for license details.

package dsptools.numbers

import chisel3._

/**
  * Much of this is drawn from non/spire, but using Chisel Bools instead of
  * Java Bools. I suppose a more general solution would be generic in
  * return type, but the use cases there seem obscure.
  */

/**
  * A simple type class for numeric types that are a subset of the reals.
  */
trait IsReal[A <: Data] extends Any with Order[A] with Signed[A] {

  /**
    * Rounds `a` the nearest integer that is greater than or equal to `a`.
    */
  def ceil(a: A): A

  /**
    * Rounds `a` the nearest integer that is less than or equal to `a`.
    */
  def floor(a: A): A

  /**
    * Rounds `a` to the nearest integer 
    * (When the fractional part is 0.5, the tie breaking method is determined by the DSP Context)
    */
  def round(a: A): A

  /**
    * Returns `true` iff `a` is a an integer.
    */
  def isWhole(a: A): Bool

}

object IsReal {
  def apply[A <: Data](implicit A: IsReal[A]): IsReal[A] = A
}

trait IsAlgebraic[A <: Data] extends Any with IsReal[A] {
}
object IsAlgebraic {
  def apply[A <: Data](implicit A: IsAlgebraic[A]): IsAlgebraic[A] = A
}

trait IsRational[A <: Data] extends Any with IsAlgebraic[A] {
}

object IsRational {
  def apply[A <: Data](implicit A: IsRational[A]): IsRational[A] = A
}

trait IsIntegral[A <: Data] extends Any with IsRational[A] {
  def ceil(a: A): A = a
  def floor(a: A): A = a
  def round(a: A): A = a
  def isWhole(a: A): Bool = true.B
  
  def mod(a: A, b: A): A

  def isOdd(a: A): Bool
  def isEven(a: A): Bool = !isOdd(a)
}

object IsIntegral {
  def apply[A <: Data](implicit A: IsIntegral[A]): IsIntegral[A] = A
}

/////////////////////////////////////////////////////////////////////////////////////

trait Real[A <: Data] extends Any with Ring[A] with ConvertableTo[A] with IsReal[A] {
  def fromRational(a: spire.math.Rational): A = fromDouble(a.toDouble)
  def fromAlgebraic(a: spire.math.Algebraic): A = fromDouble(a.toDouble)
  def fromReal(a: spire.math.Real): A = fromDouble(a.toDouble)
}

object Real {
  def apply[A <: Data](implicit A: Real[A]): Real[A] = A
}

/**
  * Much of this is drawn from non/spire, but using Chisel Bools instead of
  * Java Bools. I suppose a more general solution would be generic in
  * return type, but the use cases there seem obscure.
  */

trait Integer[A <: Data] extends Any with Real[A] with IsIntegral[A]

object Integer {
  @inline final def apply[A <: Data](implicit ev: Integer[A]): Integer[A] = ev
}
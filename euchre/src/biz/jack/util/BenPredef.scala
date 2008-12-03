package biz.jack.util

object BenPredef {
  def equal[A, B <: A](a : A, b : B) = a == b
  class NeverNull[A](val value : A) {
    require(value != null, "Error never nulls cannot contain null values")
  }
  implicit def neverNull2Any[A](a : NeverNull[A]) : A = a.value
}

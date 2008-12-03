package biz.jack.util

object BenPredef {
  def equal[A, B <: A](a : A, b : B) = a == b
}

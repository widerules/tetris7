package biz.jack.util

object BenPredef {
  def equal[A, B <: A](a : A, b : B) = a == b
  
  case class BenRichBoolean(b : Boolean) {
    def ifElse[A](t:A,f:A) = if (b) t else f
  }
  
  implicit def boolean2BRB(b : Boolean) = BenRichBoolean(b)
  
  /*
  case class NeverNull[A](val value : A) {
    require(value != null, "Error never nulls cannot contain null values")
  }
  implicit def neverNull2Any[A](a : NeverNull[A]) : A = a.value
  implicit def any2NeverNull[A](a : A) : NeverNull[A] = NeverNull(a)
   */
}

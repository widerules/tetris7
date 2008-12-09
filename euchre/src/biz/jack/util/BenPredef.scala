package biz.jack.util

object BenPredef {
  def equal[A, B <: A](a : A, b : B) = a == b
  
  case class BenRichBoolean(b : Boolean) {
    def ifElse[A](t:A,f:A) = if (b) t else f
  }
  
  implicit def boolean2BRB(b : Boolean) = BenRichBoolean(b)
  
  def shuffle[A](list : List[A], randy : java.util.Random) : List[A] = {
    val array = list.toArray;
    var i = 0;
    while (i < array.size) {
      val t = array(i)
      val offset = randy.nextInt(array.size)
      array(i) = array(offset)
      array(offset) = t
      i+=1;
    }
    array.toList
  }
  
  def rotate[A](list : List[A], times : Int) : List[A] = {
    require(times >= 0, "Rotations must be > 0")
    if (times <= 0 || list == Nil) {
      list
    } else {
      rotate(list.tail:::list.head::Nil, times - 1)
    }
  }
  /*
  case class NeverNull[A](val value : A) {
    require(value != null, "Error never nulls cannot contain null values")
  }
  implicit def neverNull2Any[A](a : NeverNull[A]) : A = a.value
  implicit def any2NeverNull[A](a : A) : NeverNull[A] = NeverNull(a)
   */
}

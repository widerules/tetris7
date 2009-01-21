package com.supersetter.schema


object Schema {
  
  case class Attribute[A](name : String, clz : Class[A])
  
  sealed case class AttributeGroup(val attributes : List[Attribute[_]])
  case object EmptyAttributeGroup extends AttributeGroup(Nil)

  sealed case class ComponentCompany(platoons : List[ComponentPlatoon[_]])
  case object EmptyComponentCompany extends ComponentCompany(Nil)
  
  sealed case class ComponentPlatoon[B](val base : Class[B])
  class SingleGroup[B, A <: B](base : Class[B], val element : Component[A]) extends ComponentPlatoon[B](base)
  class OrderedGroup[B, A <: B](base : Class[B], val elements : Component[A]*) extends ComponentPlatoon[B](base)
  class KeyedGroup[B, A <: B, R](base : Class[B], val components : KeyedComponent[R,A]) extends ComponentPlatoon[B](base)
  
  sealed abstract trait Component[A]{
    val clz : Class[A]
    
  }
  abstract class KeyedComponent[A, K](val keyClz : Class[K], val clz : Class[A]) extends Component[A] {
    val key : Attribute[K]
  }
  abstract class StringKeyedComponent[A](clz : Class[A]) extends KeyedComponent[A, String](classOf[String], clz) {
    val key : Attribute[String]
  }
}

/** A schema list all of the objects needed by the program. You should inherit off of a schema
 * class and create the appropriate mappings needed for your program
 */
class Schema {
  
}

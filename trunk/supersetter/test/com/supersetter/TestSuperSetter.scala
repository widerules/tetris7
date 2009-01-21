package com.supersetter

//Class for unit testing the super setter
import org.scalatest.FunSuite

//Stuff just needed for testing the framework
private[supersetter] object Sample {
  
  case class EmployeeManager(var employees : Map[String, Employee])
  case class CustomerManager(var customers : Map[String, Customer])

  abstract class Employee(val id : String, var wage : Int)
  class Executive(id : String, wage : Int) extends Employee(id, wage)
  class Engineer(id : String, wage : Int) extends Employee(id, wage)
  
  abstract case class Customer(val id : String, var purchases : Int)
  class Valued(id : String, wage : Int) extends Customer(id, wage)
  class Common(id : String, wage : Int) extends Customer(id, wage)
  
  val Managers : List[Class[_]] = List(classOf[EmployeeManager], classOf[CustomerManager])
  
  /** This class contains a sample scala source
   */
  class C1 extends SuperSetter.BaseSettings {
    val body = {
      <EmployeeManager>
        <Executive id="Bob" wage="5"/>
        {ss.add(new Employee("AnonymousEmployee",4){})}
        <Engineer id="Bill" wage="3"/>
      </EmployeeManager>
      <CustomerManager>
      </CustomerManager>
    }
    ss.setRoot(<SuperSettings>{body}</SuperSettings>)
  }
}


class TestSuperSetter extends FunSuite {
  test("Test testing") {
    println("FooBar")
  }
}

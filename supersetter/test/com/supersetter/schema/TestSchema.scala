package com.supersetter.schema

import org.scalatest.FunSuite
import com.supersetter.schema._

object TestSchema {
  
  object SampleSchema extends Schema {
    import Sample._
    root {
      manager(classOf[EmployeeManager]) {
        sKeyed(classOf[Employee]) {
          component(classOf[Executive]) {
            override val id = astr("id")
            val wage = aint("wage")
            create {
              new Executive(id.get, wage.get)
            } 
            update { old =>
              
            } 
          }
          component(classOf[Engineer]) {
            override val id = astr("id")
            aint("wage")
          }
        }
      }
      
    }
  }
  
}

class TestSchema extends FunSuite {

}

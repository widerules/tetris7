/*
 * Copyright 2001-2008 Artima, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.scalatest

import scala.collection.mutable.ListBuffer

trait MustStackBehaviors extends MustMatchers { this: Spec with SpecDasher =>

  val full = 'full

  def nonEmptyStack(lastItemAdded: Int)(stack: Stack[Int]) {

    "must be non-empty" - {
      stack mustNot be ('empty)
    }  

    "must return the top item on peek" - {
      stack.peek must equal (lastItemAdded)
    }
  
    "must not remove the top item on peek" - {
      val size = stack.size
      stack.peek must equal (lastItemAdded)
      stack.size must equal (size)
    }
  
    "must remove the top item on pop" - {
      val size = stack.size
      stack.pop must equal (lastItemAdded)
      stack.size must equal (size - 1)
    }
  }
  
  def nonFullStack(stack: Stack[Int]) {
      
    "must not be full" - {
      stack mustNot be (full)
    }

    "must add to the top on push" - {
      val size = stack.size
      stack.push(7)
      stack.size must equal (size + 1)
      stack.peek must equal (7)
    }
  }
}

class MustStackSpec extends Spec with SpecDasher with MustMatchers with StackFixtureCreationMethods with MustStackBehaviors {

  "A Stack" -- {

    "(when empty)" -- {
      
      "must be empty" - {
        emptyStack must be ('empty)
      }

      "must complain on peek" - {
        intercept[IllegalStateException] {
          emptyStack.peek
        }
      }

      "must complain on pop" - {
        intercept[IllegalStateException] {
          emptyStack.pop
        }
      }
    }

    "(with one item)" -- {
      stackWithOneItem must behave like nonEmptyStack(lastValuePushed)
      stackWithOneItem must behave like nonFullStack
    }
    
    "(with one item less than capacity)"-- {
      stackWithOneItemLessThanCapacity must behave like nonEmptyStack(lastValuePushed)
      stackWithOneItemLessThanCapacity must behave like nonFullStack
    }

    "(full)" -- {
      
      // fullStack must be full  .... could I get this to print the message "- must be full" ?
      "must be full" - {
        fullStack must be (full)
      }
      
      fullStack must behave like nonEmptyStack(lastValuePushed)

      "must complain on a push" - {
        intercept[IllegalStateException] {
          fullStack.push(10)
        }
      }
    }
  }
}
 

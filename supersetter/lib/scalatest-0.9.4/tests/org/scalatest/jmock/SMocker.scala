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
package org.scalatest.jmock;

import org.specs.mock.JMocker
import org.specs.mock.JMocker._
 
trait SMocker extends JMocker{

  // I have no idea what this is for. No documentation. Want to dump this stuff.
  def addExpectation: org.specs.specification.Example = null

  def expecting(block: => Any) = expect{ block }
  
  def expecting( desc: String )(block: => Any) = expect{ block }

  def when(block: => Any) = block
  
  def when( desc: String )(block: => Any) = block
  
  def withMock(f: => Unit): Unit = {
    try{
      restart
      f
      context.assertIsSatisfied
    }catch { 
      case e: org.jmock.api.ExpectationError => {
        throw new Exception(e)
      }
    }
  }
}





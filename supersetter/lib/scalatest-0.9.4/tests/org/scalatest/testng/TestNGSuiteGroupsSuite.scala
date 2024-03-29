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
package org.scalatest.testng {

   import org.scalatest.Suite
   import org.scalatest.jmock.TestReporter
   import org.testng.annotations.Test
   import testng.test._

   //execute(None, new StandardOutReporter, new Stopper {}, Set(), Set(IgnoreAnnotation), Map(), None)
   class TestNGSuiteGroupsSuite extends FunSuite {
     
     //////////////////////////////////////////////////////////
     // TESTS FOR INCLUDE
     //////////////////////////////////////////////////////////
     
     test("Groups with one method should run") {
       testIncludeGroups(Set("runMe"), 1)
     } 
     
     test( "Groups with more than one method should run" ){ testIncludeGroups(Set("runMeToo"), 2 ) }     
     
     test( "When specifically specifying to use more than one group, each group given should run" ){ 
       testIncludeGroups(Set("runMe, runMeToo"), 3 ) 
     }     

     test( "When groups are not given, all groups should run" ){ testIncludeGroups(Set(), 4 ) }    
     
     test( "Groups that doesnt exist should not do anything?" ){ testIncludeGroups(Set("groupThatDoesntExist"), 0 ) } 
     

     //////////////////////////////////////////////////////////
     // TESTS FOR EXCLUDE
     //////////////////////////////////////////////////////////     
     
     test( "Groups with one method should be excluded" ){ testExcludeGroups(Set("runMe"), 3 ) } 
     
     test( "Groups with more than one method should be excluded" ){ testExcludeGroups(Set("runMeToo"), 2 ) }       
     
     test( "When specifically specifying to exclude more than one group, those groups should not run" ){ 
       testExcludeGroups(Set("runMe, runMeToo"), 1 ) 
     } 
     
     test( "Excluding groups that dont exist shouldnt have and effect" ){ 
       testExcludeGroups(Set("groupThatDoesntExist"), 4 ) 
     } 
     
     test( "Excluding all groups should produce Zero successful tests" ){ 
       testExcludeGroups(Set("runMe", "runMeToo", "runMeThree"), 0 ) 
     }      
     
     ////////////////////////////////////////////////////////// 
     // TESTS FOR INCLUDE AND EXCLUDE
     //////////////////////////////////////////////////////////     
     
     test( "Same group in include and exclude should produce Zero successful tests" ){ 
       testGroups(Set("runMe"), Set("runMe"), 0 ) 
     } 
       
     test( "Same group in include and exclude, " + 
       "but include also includes a group with one method and it should be ran" ){ 
       testGroups(Set("runMe", "runMeThree"), Set("runMe"), 1 ) 
     } 
       
     ////////////////////////////////////////////////////////// 
       
     
     def testIncludeGroups( groupsToInclude: Set[String], successCount: int ) = { 
       testGroups( groupsToInclude, Set(), successCount ) 
     }
     
     def testExcludeGroups( groupsToExclude: Set[String], successCount: int ) = { 
       testGroups( Set(), groupsToExclude, successCount ) 
     }       
     
     def testGroups( groupsToInclude: Set[String], groupsToExclude: Set[String], successCount: int ) = {
       // given
       val testReporter = new TestReporter

       // when
       new TestNGSuiteWithGroups().runTestNG(None, testReporter, groupsToInclude, groupsToExclude)

       // then
       assert( testReporter.successCount === successCount )
     }

   }

   package test{
     
     class TestNGSuiteWithGroups extends TestNGSuite {
       @Test{val groups=Array("runMe")} def testThatRuns() {}
       @Test{val groups=Array("runMeToo")} def testThatRunsInAnotherGroup() {}
       @Test{val groups=Array("runMeToo")} def anotherTestThatRunsInAnotherGroup() {}
       @Test{val groups=Array("runMeThree")} def yetAnotherTestThatRunsInYetAnotherGroup() {}
     }
   }
}



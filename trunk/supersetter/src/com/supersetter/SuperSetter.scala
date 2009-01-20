package com.supersetter

/** The super setter class will support a dynamic xml based configuration setup.
 * additional variables can be specified by the clients in additional files.
 * Import chaining will be support, and final overriding will also be attempted
 * 
 * Copyright Ben Jackman 2009
 */
object SuperSetter {
  //Parses a scala file into a tree, the scala file must call the 
  //set node function at some point in time
  //Additional any properties files used by the file should
  //have an addProperties() call associated with them
  def fromScala(file : String) : SuperTree = {
    //Get a scala interpreter and run it
    SuperTree(SuperTreeNode(Nil, Nil))
  }
  
  case class SuperTree(val root : SuperTreeNode) {
  
  }

  case class SuperTreeAttribute(val name : String, val value : String) {
  
  }

  case class SuperTreeNode(val children : List[SuperTreeNode], val attributes : List[SuperTreeAttribute]) {
  
  }
}


class SuperSetter {
  
}

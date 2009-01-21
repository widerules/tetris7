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
    tools.nsc.Interpreter
    SuperTree(EmptyNode)
  }
  
  case class SuperTree(val root : SuperTreeNode) {
    
  }

  case class SuperTreeAttribute(val name : String, val value : String) {
  
  }

  sealed abstract class SuperTreeNode {
    val id : Option[String]
  }
  
  //Simple place holder for nodes that are empty
  case object EmptyNode extends SuperTreeNode{
    val id = None
  }
  
  //XML Tree Nodes are nodes that use xml to map over the components
  //fields and sub components
  case class XMLNode(id : Option[String], children : List[SuperTreeNode], val attributes : List[SuperTreeAttribute]) 
  extends SuperTreeNode {
    
  }
  
  //Object tree node allow for dynamically created components,
  //that is components that are created at program runtime
  //rather than at project compile time
  //They allow allow for instantiating components directly
  case class AnyRefNode(id : Option[String], ref : AnyRef) {
    
  }
}

class SuperSetter {
  
}

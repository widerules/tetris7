package com.supersetter

/** The super setter class will support a dynamic xml based configuration setup.
 * additional variables can be specified by the clients in additional files.
 * Import chaining will be support, and final overriding will also be attempted
 * 
 * Copyright Ben Jackman 2009
 */
object SuperSetter {
  val Log = biz.jackman.logging.Logger("SuperSetter")
  //Parses a scala file into a tree, the scala file must call the 
  //set node function at some point in time
  //Additional any properties files used by the file should
  //have an addProperties() call associated with them
  def fromScript(file : String) : SuperTree = {
    //Get a scala interpreter and run it
    val interpreter = mkInterpreter 
    
    SuperTree(EmptyNode)
  }
  
  //Used for xml based trees
  class LibraryFunctions {
    var root : Option[AnyRef] = None
    var nextId = 0L
    var injected : Map[Long, AnyRef] = Map()
    
    //This function must be called by a script to
    //tell the super setter what element is going to end
    //up being the root element
    def setRoot(ref : AnyRef) { this.root = Some(ref) }
    
    //This function allows for adding objects directly
    //into the tree
    def add(ref : AnyRef) : xml.Elem = {
      val id = nextId
      nextId += 1
      injected += id -> ref
      (<ObjectRef id={id.toString}/>)
    } 
  }
  
  def mkInterpreter {
    val interpreter = supersetter.interpreter.SSInterpreter.createInterpreter(Log.error(_))
    
    //Add the core components to the interpeter
    {
      interpreter.bind("SS", classOf[LibraryFunctions].getName, new LibraryFunctions)
    }
    
    interpreter
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

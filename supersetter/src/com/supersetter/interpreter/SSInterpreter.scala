package com.supersetter.interpreter

//Wrapper that avoids having to the imports for the interpreter
object SSInterpreter {
  import scala.tools.nsc.{Interpreter, Settings, InterpreterResults}
  import java.io.{PipedWriter, PrintWriter}

  //Makes a quiet interpreter that puts it output to standard out
  def createInterpreter(errorWriter : (String) => Unit) : Interpreter = {
    val i = new Interpreter(new Settings(errorWriter), new PrintWriter(System.out))
    i.beQuiet
    i
  }
  
  def createInterpreter(errorWriter : (String) => Unit, out : (String) => Unit) : Interpreter = {
    val pipe = new PipedWriter
    new Interpreter(new Settings(errorWriter), new PrintWriter(pipe))
  }
  
}

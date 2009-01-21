package biz.jackman.logging

//The logging packages, designed specifically to support the lazy evaluation style
//espoused by scala
//To create a logger you probably want to declare 
//val Log = biz.jackman.logging.Logger("SomeCategory")
//inside of a companion object to the class you are going to use the logger for
//from the on you can call
//Log.debug {
//		"I am logging something!\n" +
//		"And I don't care who knows it!"
//}
//
//Log.error("Beep Beep Error Error Error!")
//
//Log.fatal(nastyException) {
// "Oh Noes! I got an exception I could not handle!"
//}
//etc.
object Logger {
  def apply[A](clz : Class[A]) = new Logger(clz.getName, LoggerRoot.getRoot _)
  def apply(category : String) = new Logger(category, LoggerRoot.getRoot _)
  case object Trace extends Level(1000)
  case object Debug extends Level(2000)
  case object Warn extends Level(3000)
  case object Error extends Level(4000)
  case object Fatal extends Level(5000)
}

//This class provides an alternate way to declare
//a logger if you do not care about categories
//It probably should not be used, however, it does
//have the advantage of allowing for a quick and dirty
//way of making a logger using only import statements
//use this syntax to make one
//import jackman.biz.logging.{CatlessLogger=>Log}
object CatlessLogger extends Logger("", LoggerRoot.getRoot _)

//Singleton class for the root of all logging messages
object LoggerRoot {
  var root : LoggerAppender = DefaultLogWriter
  def getRoot = root
}

abstract class Level(val severity : Int)

import Logger._

class Logger(val category : String, val outNode : () => LoggerAppender) {
  def out = outNode()
  
  private def logInternal(severity : Int, msg : => String, t : Option[Throwable]) = 
    out.log(LogEvent(None, None, System.currentTimeMillis, category, severity, {()=>msg}, t))
  
  def log(severity : Int)(msg : => String) = logInternal(severity, msg, None)
  def log(severity : Int, t : Throwable)(msg : => String) = logInternal(severity, msg, Some(t))
  
  def log(level : Level)(msg : => String) = logInternal(level.severity, msg, None)
  def log(level : Level, t : Throwable)(msg : => String) = logInternal(level.severity, msg, Some(t))
  
  def trace(msg : => String) = log(Trace)(msg)
  def trace(t : Throwable)(msg : => String) = log(Trace,t)(msg)
  
  def debug(msg : => String) = log(Debug)(msg)
  def debug(t : Throwable)(msg : => String) = log(Debug, t)(msg)
  
  def warn(msg : => String) = log(Warn)(msg)
  def warn(t : Throwable)(msg : => String) = log(Warn,t)(msg)
  
  def error(msg : => String) = log(Error)(msg)
  def error(t : Throwable)(msg : => String) = log(Error,t)(msg)
  
  def fatal(msg : => String) = log(Fatal)(msg)
  def fatal(t : Throwable)(msg : => String) = log(Fatal,t)(msg)
}

abstract class LoggerNode(val children : List[LoggerAppender]) extends LoggerAppender {
  override def logsCategory(category : String) = true
  override lazy val lowestSeverity : Int = children match {
    case Nil => Int.MaxValue
    case xs => children.foldLeft[Int](myLowestSeverity)((low,a) => java.lang.Math.min(low, a.lowestSeverity))
  }
  override lazy val needsThread : Boolean = iNeedThread || children.find(_.needsThread) != None
  override lazy val needsNanos : Boolean = iNeedNanos || children.find(_.needsNanos) != None

  override def log(event : LogEvent) {
    //ugh what a mess, basically, we only want to get the currentThread, or nanos when we have to
    val event_ : LogEvent = if (needsThread && !event.thread.isDefined && needsNanos && !event.nanos.isDefined) {
      LogEvent(Some(Thread.currentThread), Some(System.nanoTime), event.timestamp, event.category, event.severity, event.msg, event.t)
    } else if (needsNanos && !event.nanos.isDefined) {
      LogEvent(None, Some(System.nanoTime), event.timestamp, event.category, event.severity, event.msg, event.t)
    } else if (needsThread && !event.thread.isDefined) {
      LogEvent(Some(Thread.currentThread), None, event.timestamp, event.category, event.severity, event.msg, event.t)
    } else {
      event
    }
    
    for (a <- children if (a.logsCategory(event.category) && a.logsSeverity(event.severity))) {
      a.log(event_)
    }
  }
  
  def myLowestSeverity : Int = Int.MaxValue 
  def iNeedThread : Boolean = false
  def iNeedNanos : Boolean = false
}

object DefaultFormatter extends LogFormatter {
  val formatter = new java.lang.ThreadLocal[java.text.DateFormat] {
    override def initialValue = new java.text.SimpleDateFormat("YYYY-mm-dd-hh:mm:ss.SSS")
  }
  override val needsThread = false
  override val needsNanos = false
  override def format(event : LogEvent) = formatter.get.format(event.timestamp) + " " + event.msg + "\n"
}

object DefaultFilter extends LogFilter {
  override def logsCategory(category : String) : Boolean = true
  override def lowestSeverity : Int = Debug.severity
}


abstract class LogWriter(val filter : LogFilter, val formatter : LogFormatter) extends LoggerAppender {
  override val needsThread : Boolean = formatter.needsThread
  override val needsNanos : Boolean = formatter.needsNanos
  override def logsCategory(category : String) : Boolean = filter.logsCategory(category)
  override val lowestSeverity : Int = filter.lowestSeverity
  override def log(event : LogEvent) : Unit = {
    write(formatter.format(event))
  }
  protected def write(string : String) : Unit
}

object DefaultLogWriter extends LogWriter(DefaultFilter, DefaultFormatter) {
  override def write(string : String) : Unit = println(string)
}

class FileLogWriter(filename : String) extends LogWriter(DefaultFilter, DefaultFormatter) {
  private val writer = new java.io.FileWriter(filename, true)
  override def write(string : String) : Unit = writer.append(string).append("\n")
}

trait LogFilter {
  def logsCategory(category : String) : Boolean
  def lowestSeverity : Int
}

trait LogFormatter {
  val needsThread : Boolean
  val needsNanos : Boolean
  def format(event : LogEvent) : String   
}

case class LogEvent(thread : Option[Thread], nanos : Option[Long], timestamp : Long, 
                    category : String, severity : Int, msg : ()=> String, t : Option[Throwable])

trait LoggerAppender {
  def logsCategory(category : String) : Boolean
  final def logsSeverity(severity : Int) : Boolean = severity >= lowestSeverity 
  def lowestSeverity : Int
  def needsThread : Boolean
  def needsNanos : Boolean
  def log(event : LogEvent) : Unit
}


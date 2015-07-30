package bitflux.core

import com.github.nscala_time.time.Imports._

class Constant[T](p: => T)(implicit context: Context) 
    extends Flow[T] 
    with RealtimeSource[T] 
    with SimulationSource[T] {
  
  private var ticked = false
  private var sent = false
  
  react(context) { // always return true
    if (!sent) {
      sent = true 
      p
    }
  }
  
  override def topTick(start: DateTime, end: DateTime): Option[(DateTime, T)] = {
    if (!ticked) {
      ticked = true
      if (getContext.getCurrentTime.nonEmpty) Some(now + Context.TimeStep, p) else Some(start, p)
    }
    else None 
  }
  
  // TODO: the timestamp might not be "start"
  override def subscribe(start: DateTime, end: DateTime): Unit = 
    getContext.asInstanceOf[RealtimeRunner].enqueue((this, start))
}

object Constant {
  def apply[T](p: T)(implicit context: Context) = new Constant(p)
}
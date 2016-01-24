package bitflux.env

import scala.concurrent.Future
import scala.concurrent.ExecutionContext

import com.github.nscala_time.time.Imports._
import bitflux.core.SequentialSimulationContext
import bitflux.core.ParallelSimulationContext
import bitflux.core.SingleLevelParallelSimulationContext

class Simulation(from: DateTime, to: DateTime, isSequential: Boolean = false)
    (implicit val executionContext: ExecutionContext = ExecutionContext.Implicits.global) {

  implicit val context = if (isSequential)
    new SequentialSimulationContext("bitflux-simulation")
  else
    new SingleLevelParallelSimulationContext("bitflux-simulation")

  def run[T](code: => T)(implicit executionContext: ExecutionContext): Future[T] = {
    Future {
      val res = code
      context.run(from, to)
      res
    }
  }
}

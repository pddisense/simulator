package ucl.simulator

import java.util.concurrent.ForkJoinPool

import scala.collection.parallel.ForkJoinTaskSupport
import scala.reflect.ClassTag

abstract class Operator[T <: Product : ClassTag] {
  def execute(ctx: OperatorContext[T]): Unit

  protected final def forEach(range: Range)(fn: Int => Unit): Unit = {
    val cores = math.max(1, (sys.runtime.availableProcessors() * 0.9).toInt)
    val parRange = range.par
    parRange.tasksupport = new ForkJoinTaskSupport(new ForkJoinPool(cores))
    parRange.foreach(fn)
  }
}

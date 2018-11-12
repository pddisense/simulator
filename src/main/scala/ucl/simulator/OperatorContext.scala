package ucl.simulator

import io.lumos.core.types.Schema
import io.lumos.sdk.LumosSdk

import scala.reflect.ClassTag
import scala.util.Random


final class OperatorContext[T <: Product : ClassTag](val seed: Long, taskName: String, sdk: LumosSdk) {
  lazy val rnd = new Random(seed)

  private[this] val schema = Schema.from(implicitly[ClassTag[T]].runtimeClass)

  lazy val output: Dataset[T] = new Dataset[T](schema, sdk.artifactsDir.resolve(taskName))

  def report(name: String, value: Long): Unit = sdk.logChannel(name, value)

  def report(name: String, value: Double): Unit = sdk.logChannel(name, value)

  def report(name: String, value: String): Unit = sdk.logChannel(name, value)

  def report(name: String, value: Boolean): Unit = sdk.logChannel(name, value)
}

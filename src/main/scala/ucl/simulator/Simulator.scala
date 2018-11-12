package ucl.simulator

import io.lumos.sdk.LumosSdk

import scala.util.control.NonFatal

object SimulatorMain extends Simulator

class Simulator {
  def main(args: Array[String]): Unit = {
    val sdk = LumosSdk.createStarted(args)
    try {
      new Workflow(sdk).execute()
    } catch {
      case NonFatal(e) => sdk.logException(e)
    } finally {
      sdk.close()
    }
  }
}

package ucl.simulator

import io.lumos.sdk.LumosSdk

import scala.reflect.ClassTag
import scala.util.Random

final class Workflow(sdk: LumosSdk) {
  private[this] val delay = sdk.getParam("delay", 0)
  private[this] val daysCount = sdk.getParam("days", 10)
  private[this] val usersCount = sdk.getParam("users", 100)
  private[this] val queriesStrategy = sdk.getParam("queries", "yahoo")
  private[this] val groupsStrategy = sdk.getParam("groups", "naive")
  private[this] val groupSize = sdk.getParam("gs", 10)

  private[this] val random = new Random(sdk.getParam("seed", Random.nextLong()))

  def execute(): Unit = {
    println(s"Starting run ${sdk.runId}")

    // Generate activity. We generate activity for `delay` + 1 more days, as
    // these are needed to properly evaluate the last days without any bias that
    // would result in very bad results the last days. The additional "+ 1" comes
    // from the fact that data is always published with at least one day of delay,
    // i.e., the search data of day `d` can only be collected on day `d + 1` at
    // the very best (we need the day to be finished to start collecting data).
    val activity = execute(GenerateActivityOperator(
      usersCount = usersCount,
      daysCount = daysCount + delay))

    val searches = execute(GenerateSearchesOperator(
      activity = activity,
      daysCount = daysCount,
      strategy = queriesStrategy))

    val memberships = execute(AssignGroupsOperator(
      activity = activity,
      usersCount = usersCount,
      daysCount = daysCount,
      maxDelay = delay,
      strategy = groupsStrategy,
      groupSize = groupSize))

    val decryptions = execute(EvalGroupsOperator(
      activity = activity,
      memberships = memberships,
      daysCount = daysCount,
      usersCount = usersCount,
      maxDelay = delay))

    val distRaw = execute(AggregateSearchesOperator(
      activity = activity,
      searches = searches,
      daysCount = daysCount))

    val distSketches = execute(AggregateSketchesOperator(
      activity = activity,
      searches = searches,
      memberships = memberships,
      decryptions = decryptions,
      daysCount = daysCount))

    val distErrors = execute(ComputeErrorOperator(
      distRaw = distRaw,
      distSketches = distSketches,
      daysCount = daysCount))

    println(s"Done.")
  }

  private def execute[T <: Product : ClassTag](step: Operator[T]): Dataset[T] = {
    val stepName = step.getClass.getSimpleName.stripSuffix("Operator")
    val startMillis = System.currentTimeMillis()
    println(s"Starting $stepName")
    val ctx = new OperatorContext[T](random.nextLong(), stepName, sdk)
    step.execute(ctx)
    sdk.logArtifact(stepName, sdk.artifactsDir.resolve(stepName), "csv")
    println(s"Completed $stepName in ${System.currentTimeMillis() - startMillis} ms")
    ctx.output
  }
}

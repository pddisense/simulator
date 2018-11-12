package ucl.simulator

import scala.collection.mutable

case class AggregateSketchesOperator(
  activity: Dataset[GenerateActivityOperator.Row],
  searches: Dataset[GenerateSearchesOperator.Row],
  memberships: Dataset[AssignGroupsOperator.Row],
  decryptions: Dataset[EvalGroupsOperator.Row],
  daysCount: Int)
  extends Operator[AggregateSketchesOperator.Row] {

  import AggregateSketchesOperator._

  override def execute(ctx: OperatorContext[Row]): Unit = {
    val metrics = Metrics(new MutableAverage)
    forEach(0 until daysCount)(execute(ctx, _, metrics))
    ctx.report("confidence", metrics.confidence.get)
  }

  private def execute(ctx: OperatorContext[Row], day: Int, metrics: Metrics): Unit = {
    // The memberships and decryptions of a given day are only made available the next day.
    val nextDay = day + 1
    val decryptedGroups = decryptions.read(nextDay.toString).map(_.group).toSet
    val decryptedUsers = mutable.Set.empty[Int]
    memberships.read(nextDay.toString).foreach { row =>
      if (decryptedGroups.contains(row.group)) {
        decryptedUsers += row.user
      }
    }

    // We have to read the total number of active users from the `activity` table,
    // as not all users may be present into groups.
    val totalUsers = activity.read(day.toString).size
    val confidence = decryptedUsers.size.toDouble / totalUsers
    metrics.confidence.add(confidence)

    // To compute the total (estimated) count, we also have to limit to activity to
    // the activity of users whose sketches have been decrypted.
    val totalCount = activity.read(day.toString)
      .filter(row => decryptedUsers.contains(row.user))
      .map(_.count)
      .sum
    val counts = mutable.Map.empty[Int, Int]
    searches.read(day.toString).filter(row => decryptedUsers.contains(row.user)).foreach { row =>
      counts.put(row.query, counts.getOrElse(row.query, 0) + 1)
    }
    val rows = counts.map { case (query, count) =>
      Row(
        day = day,
        query = query,
        count = count,
        totalCount = totalCount,
        frequency = count.toDouble / totalCount,
        confidence = confidence)
    }
    ctx.output.write(day.toString, rows)
  }
}

object AggregateSketchesOperator {

  case class Row(day: Int, query: Int, count: Int, totalCount: Int, frequency: Double, confidence: Double)

  private case class Metrics(confidence: MutableAverage)

}
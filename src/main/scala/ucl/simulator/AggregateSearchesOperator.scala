package ucl.simulator

import scala.collection.mutable

case class AggregateSearchesOperator(
  activity: Dataset[GenerateActivityOperator.Row],
  searches: Dataset[GenerateSearchesOperator.Row],
  daysCount: Int)
  extends Operator[AggregateSearchesOperator.Row] {

  import AggregateSearchesOperator._

  override def execute(ctx: OperatorContext[Row]): Unit = {
    forEach(0 until daysCount)(execute(ctx, _))
  }

  private def execute(ctx: OperatorContext[Row], day: Int): Unit = {
    val counts = mutable.Map.empty[Int, Int]
    searches.read(day.toString).foreach { row =>
      counts.put(row.query, counts.getOrElse(row.query, 0) + row.count)
    }
    val totalCount = activity.read(day.toString).map(_.count).sum
    val rows = counts.map { case (query, count) =>
      Row(
        day = day,
        query = query,
        count = count,
        totalCount = totalCount,
        frequency = count.toDouble / totalCount)
    }
    ctx.output.write(day.toString, rows)
  }
}

object AggregateSearchesOperator {

  case class Row(day: Int, query: Int, count: Int, totalCount: Int, frequency: Double)

}
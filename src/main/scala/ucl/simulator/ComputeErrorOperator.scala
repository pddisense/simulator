package ucl.simulator

import java.util.concurrent.atomic.AtomicLong

import com.google.common.util.concurrent.AtomicDouble

import scala.collection.mutable

case class ComputeErrorOperator(
  distRaw: Dataset[AggregateSearchesOperator.Row],
  distSketches: Dataset[AggregateSketchesOperator.Row],
  daysCount: Int)
  extends Operator[ComputeErrorOperator.Row] {

  import ComputeErrorOperator._

  override def execute(ctx: OperatorContext[Row]): Unit = {
    val metrics = Metrics(new MutableAverage, new MutableAverage, new MutableAverage, new MutableAverage)
    forEach(0 until daysCount)(execute(ctx, _, metrics))
    ctx.report("count_mae", metrics.countMAE.get)
    ctx.report("count_mre", metrics.countMRE.get)
    ctx.report("freq_mae", metrics.freqMAE.get)
    ctx.report("freq_mre", metrics.freqMRE.get)
  }

  private def execute(ctx: OperatorContext[Row], day: Int, metrics: Metrics): Unit = {
    val sketchIndex = mutable.Map.empty[Int, AggregateSketchesOperator.Row]
    distSketches.read(day.toString).foreach(row => sketchIndex.put(row.query, row))
    val estimatedTotalCount = sketchIndex.values.headOption.map(_.totalCount).getOrElse(0)
    val rows = distRaw.read(day.toString).map { row =>
      val sketch = sketchIndex.getOrElse(
        row.query,
        AggregateSketchesOperator.Row(day, row.query, 0, totalCount = estimatedTotalCount, 0, -1))

      val countAbsError = row.count - sketch.count
      val countRelError = countAbsError.toDouble / row.count
      metrics.countMAE.add(countAbsError)
      metrics.countMRE.add(countRelError)

      val freqAbsError = row.frequency - sketch.frequency
      val freqRelError = freqAbsError / row.frequency
      metrics.freqMAE.add(freqAbsError)
      metrics.freqMRE.add(freqRelError)

      Row(
        day = day,
        query = row.query,
        trueCount = row.count,
        trueTotalCount = row.totalCount,
        estimatedCount = sketch.count,
        estimatedTotalCount = sketch.totalCount,
        trueFreq = row.frequency,
        estimatedFreq = sketch.frequency,
        countAbsError = countAbsError,
        countRelError = countRelError,
        freqAbsError = freqAbsError,
        freqRelError = freqRelError)
    }
    ctx.output.write(day.toString, rows)
  }

}

object ComputeErrorOperator {

  case class Row(
    day: Int,
    query: Int,
    trueCount: Int,
    trueTotalCount: Int,
    estimatedCount: Int,
    estimatedTotalCount: Int,
    trueFreq: Double,
    estimatedFreq: Double,
    countAbsError: Int,
    freqAbsError: Double,
    countRelError: Double,
    freqRelError: Double)

  private case class Metrics(
    countMRE: MutableAverage,
    countMAE: MutableAverage,
    freqMRE: MutableAverage,
    freqMAE: MutableAverage)

}

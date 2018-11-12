package ucl.simulator

import org.apache.commons.math3.random.{MersenneTwister, RandomGenerator}

import scala.collection.mutable
import scala.io.Source

case class GenerateSearchesOperator(
  activity: Dataset[GenerateActivityOperator.Row],
  daysCount: Int,
  strategy: String)
  extends Operator[GenerateSearchesOperator.Row] {

  import GenerateSearchesOperator._

  override def execute(ctx: OperatorContext[Row]): Unit = {
    val queries = strategy match {
      case "yahoo" => readYahooQueries()
      case "google" => readGoogleQueries()
      case unknown => throw new RuntimeException(s"Unknown strategy: $unknown")
    }
    val seeds = Seq.fill(daysCount)(ctx.rnd.nextLong())
    forEach(0 until daysCount) { day =>
      execute(ctx, day, queries, seeds(day))
    }
  }

  private def readYahooQueries(): Seq[Query] = {
    val source = Source.fromResource("yahoo-dist.csv")
    val res = source.getLines().map { line =>
      val parts = line.trim.split(',')
      Query(parts.head, new ConstantDistribution(parts(1).toDouble))
    }.toList
    source.close()
    res
  }

  private def readGoogleQueries(): Seq[Query] = {
    val source = Source.fromResource("google-dist.csv")
    val res = source.getLines().map { line =>
      val parts = line.trim.split(',')
      parts(1) match {
        case "Beta" =>
          val dist = new BetaDistribution(parts(2).toDouble, parts(3).toDouble)
          Query(parts.head, dist)
        case "gev" =>
          val dist = new GEVDistribution(parts(2).toDouble, parts(3).toDouble, parts(4).toDouble)
          Query(parts.head, dist)
        case unknown => throw new RuntimeException(s"Unknown distribution: $unknown")
      }
    }.toList
    source.close()
    res
  }

  private def sampleFrequencies(queries: Seq[Query], rnd: RandomGenerator): Seq[Double] = {
    val frequencies = mutable.ListBuffer.empty[Double]
    var acc = 0d
    queries.foreach { q =>
      acc += sample(q.dist, rnd)
      frequencies += acc
    }
    frequencies.toList
  }

  private def sampleSearches(rnd: RandomGenerator, frequencies: Seq[Double], count: Long): Seq[Int] = {
    (0L until count).flatMap { _ =>
      val v = rnd.nextDouble()
      if (frequencies.last <= v) {
        // This highly speeds up the processing, as in most cases not tracked query will be generated.
        None
      } else {
        val q = frequencies.indexWhere(_ > v)
        if (q > -1) Some(q) else None
      }
    }
  }

  private def sample(dist: RealDistribution, rnd: RandomGenerator): Double = {
    val sampler = dist.newSampler(rnd)
    var res = -1d
    do {
      res = sampler.sample()
    } while (res < 0 || res > 1)
    res
  }

  private def execute(ctx: OperatorContext[Row], day: Int, queries: Seq[Query], seed: Long): Unit = {
    val rnd = new MersenneTwister(seed)
    val frequencies = sampleFrequencies(queries, rnd)
    val rows = activity.read(day.toString).flatMap { row =>
      val searches = sampleSearches(rnd, frequencies, row.count)
      val counts = searches.groupBy(identity).map { case (k, v) => k -> v.size }
      counts.map { case (q, count) => Row(user = row.user, day = day, query = q, count = count) }
    }
    ctx.output.write(day.toString, rows)
  }
}

object GenerateSearchesOperator {

  case class Row(user: Int, day: Int, query: Int, count: Int)

  private case class Query(q: String, dist: RealDistribution)

}
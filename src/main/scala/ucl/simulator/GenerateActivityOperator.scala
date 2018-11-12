package ucl.simulator

import org.apache.commons.math3.random.MersenneTwister
import org.apache.commons.math3.util.MathArrays

/**
 * Simulate activity for users over a given period of time.
 *
 * Whether users are active on a given day is drawn from a Bernouilli/binomial
 * distribution, meaning there is a fixed probability for every user to be
 * active on a given day.
 *
 * The number of daily searches made by active users is drawn from a Poisson
 * distribution, according to a fixed rate.
 *
 * @param usersCount Number of users for which to generate activity.
 * @param daysCount  Number of days for which to generate activity.
 */
case class GenerateActivityOperator(usersCount: Int, daysCount: Int)
  extends Operator[GenerateActivityOperator.Row] {

  private[this] val onlineDist = new BetaDistribution(2.216968764644542, 0.46345530504869703)
  private[this] val volumeDist = new NormalDistribution(1.3019559156423528, 0.7602950482329748)

  import GenerateActivityOperator._

  override def execute(ctx: OperatorContext[Row]): Unit = {
    val activityTensor = generateActivityTensor(ctx)

    val seeds = Seq.fill(daysCount + 1)(ctx.rnd.nextLong())
    // Groups of a day `d` are used to collect searches of day `d - 1`.
    // Therefore, we generate activity for 1 more day.
    forEach(0 until (daysCount + 1))(day => execute(ctx, day, activityTensor, seeds(day)))
  }

  private def execute(ctx: OperatorContext[Row], day: Int, activityTensor: Array[Array[Boolean]], seed: Long): Unit = {
    val sampler = volumeDist.newSampler(new MersenneTwister(seed))
    val rows = (0 until usersCount).iterator.flatMap { user =>
      if (activityTensor(user)(day)) {
        val count = math.max(0, sampler.sample().round.toInt)
        Seq(Row(user = user, day = day, count = count))
      } else {
        Seq.empty
      }
    }
    ctx.output.write(day.toString, rows)
  }

  private def generateActivityTensor(ctx: OperatorContext[Row]): Array[Array[Boolean]] = {
    val seeds = Seq.fill(usersCount)(ctx.rnd.nextLong())
    val result = Array.fill(usersCount)(Array.fill(daysCount + 1)(false))
    forEach(0 until usersCount) { user =>
      val rnd = new MersenneTwister(seeds(user))
      val sampler = onlineDist.newSampler(rnd)
      val activeDaysCount = math.max(0, math.min(daysCount + 1, math.round(sampler.sample() * (daysCount + 1)).toInt))
      val activeDays = if (activeDaysCount > 0) {
        val days = (0 until (daysCount + 1)).toArray
        MathArrays.shuffle(days, rnd)
        days.take(activeDaysCount)
      } else {
        Array.emptyIntArray
      }
      activeDays.foreach(day => result(user)(day) = true)
    }
    result
  }
}

object GenerateActivityOperator {

  /**
   * A row that indicates that a given user was active on a given day, and performed a given
   * number of searches (which may be zero).
   *
   * @param user
   * @param day
   * @param count
   */
  case class Row(user: Int, day: Int, count: Int)

}
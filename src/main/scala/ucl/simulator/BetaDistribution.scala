package ucl.simulator

import org.apache.commons.math3.distribution.{BetaDistribution => ApacheBetaDistribution}
import org.apache.commons.math3.random.RandomGenerator

final class BetaDistribution(a: Double, b: Double) extends RealDistribution {
  override def newSampler(rnd: RandomGenerator): RealDistribution.Sampler = {
    val impl = new ApacheBetaDistribution(rnd, a, b, ApacheBetaDistribution.DEFAULT_INVERSE_ABSOLUTE_ACCURACY)
    () => impl.sample()
  }
}
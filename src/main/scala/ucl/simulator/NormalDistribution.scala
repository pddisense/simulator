package ucl.simulator

import org.apache.commons.math3.distribution.{NormalDistribution => ApacheNormalDistribution}
import org.apache.commons.math3.random.RandomGenerator

final class NormalDistribution(mean: Double, stddev: Double) extends RealDistribution {
  override def newSampler(rnd: RandomGenerator): RealDistribution.Sampler = {
    val impl = new ApacheNormalDistribution(rnd, mean, stddev)
    () => impl.sample()
  }
}
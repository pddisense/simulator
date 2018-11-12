package ucl.simulator

import org.apache.commons.math3.random.RandomGenerator

trait RealDistribution {
  def newSampler(rnd: RandomGenerator): RealDistribution.Sampler
}

object RealDistribution {
  trait Sampler {
    def sample(): Double
  }
}
package ucl.simulator
import org.apache.commons.math3.random.RandomGenerator

final class ConstantDistribution(v: Double) extends RealDistribution {
  override def newSampler(rnd: RandomGenerator): RealDistribution.Sampler = () => v
}

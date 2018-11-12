package ucl.simulator

import org.apache.commons.math3.random.RandomGenerator

// TODO: compare with scipy impl
// https://github.com/scipy/scipy/blob/994e74b441085a8d2a0f53a2f0f10cabde584918/scipy/stats/_distn_infrastructure.py
// https://github.com/scipy/scipy/blob/994e74b441085a8d2a0f53a2f0f10cabde584918/scipy/stats/_continuous_distns.py
// OR
// https://bitbucket.org/alesia/jdistlib/src/74d3198ee6341d87533d55ea867994dcbe433442/src/main/java/jdistlib/evd/GEV.java?at=default&fileviewer=file-view-default
// https://bitbucket.org/alesia/jdistlib/src/74d3198ee6341d87533d55ea867994dcbe433442/src/main/java/jdistlib/Exponential.java?at=default&fileviewer=file-view-default
final class GEVDistribution(loc: Double, scale: Double, shape: Double) extends RealDistribution {
  override def newSampler(rnd: RandomGenerator): RealDistribution.Sampler = () => {
    if (scale < 0) {
      Double.NaN
    } else {
      loc + scale * ((math.pow(-math.log(rnd.nextDouble()), -shape) - 1) / shape)
    }
  }
}

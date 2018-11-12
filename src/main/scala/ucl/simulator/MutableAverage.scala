package ucl.simulator

import java.util.concurrent.atomic.AtomicLong

import com.google.common.util.concurrent.AtomicDouble

private[simulator] class MutableAverage {
  private[this] val sum = new AtomicDouble()
  private[this] val n = new AtomicLong()

  def add(v: Double): Unit = {
    sum.addAndGet(v.abs)
    n.incrementAndGet()
  }

  def get: Double = sum.get() / n.get()
}
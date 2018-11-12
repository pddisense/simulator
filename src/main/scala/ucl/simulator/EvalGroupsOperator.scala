package ucl.simulator

import scala.collection.mutable

case class EvalGroupsOperator(
  activity: Dataset[GenerateActivityOperator.Row],
  memberships: Dataset[AssignGroupsOperator.Row],
  usersCount: Int,
  daysCount: Int,
  maxDelay: Int)
  extends Operator[EvalGroupsOperator.Row] {

  import EvalGroupsOperator._

  override def execute(ctx: OperatorContext[Row]): Unit = {
    val activityTensor = readActivityTensor()
    forEach(1 to daysCount)(evaluate(ctx, _, activityTensor))
  }

  private def evaluate(ctx: OperatorContext[Row], day: Int, activityTensor: Array[Array[Boolean]]): Unit = {
    val delayByGroup = mutable.Map.empty[Int, Int]
    val invalidGroups = mutable.Set.empty[Int]
    memberships.read(day.toString).foreach { row =>
      if (!invalidGroups.contains(row.group)) {
        val prevDelay = delayByGroup.getOrElse(row.group, 0)
        val activeDays = activityTensor(row.user)
          .slice(day, day + maxDelay + 1)
          .zipWithIndex
          .toSet
          .filter(_._1)
        if (activeDays.nonEmpty) {
          // User has been active, it does not invalidate his group.
          // User individual delay is the first day he has been seen, group delay is the first
          // day all users have been seen at least once.
          val userDelay = activeDays.head._2
          delayByGroup.put(row.group, math.max(prevDelay, userDelay))
        } else {
          // User has been inactive, this group is discarded.
          delayByGroup.remove(row.group)
          invalidGroups.add(row.group)
        }
      }
    }
    val res = delayByGroup.iterator.map { case (group, delay) =>
      Row(group = group, day = day, delay = delay)
    }
    ctx.output.write(day.toString, res)
  }

  private def readActivityTensor(): Array[Array[Boolean]] = {
    val result = Array.fill(usersCount)(Array.fill[Boolean](daysCount + maxDelay + 1)(false))
    forEach(0 to daysCount + maxDelay) { day =>
      activity.read(day.toString).foreach { row =>
        result(row.user)(day) = true
      }
    }
    result
  }
}

object EvalGroupsOperator {

  case class Row(group: Int, day: Int, delay: Int)

}
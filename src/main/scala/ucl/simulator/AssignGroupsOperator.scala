package ucl.simulator

import scala.collection.mutable

case class AssignGroupsOperator(
  activity: Dataset[GenerateActivityOperator.Row],
  usersCount: Int,
  daysCount: Int,
  maxDelay: Int,
  strategy: String,
  groupSize: Int)
  extends Operator[AssignGroupsOperator.Row] {

  import AssignGroupsOperator._

  override def execute(ctx: OperatorContext[Row]): Unit = {
    val activityTensor = readActivityTensor()
    // Groups of a day `d` are used to collect searches of day `d - 1`. Therefore, we start
    // generating only on the second day and go one day further after the total number of days.
    forEach(1 to daysCount)(execute(ctx, _, activityTensor))
  }

  private def execute(ctx: OperatorContext[Row], day: Int, activityTensor: Array[Array[Boolean]]): Unit = {
    strategy match {
      case "naive" => assignNaive(ctx, day, activityTensor)
      case "prune" => assignPrune(ctx, day, activityTensor)
      case unknown => throw new RuntimeException(s"Unknown strategy: $unknown")
    }
  }

  private def assignNaive(ctx: OperatorContext[Row], day: Int, activityTensor: Array[Array[Boolean]]): Unit = {
    val yesterday = day - 1
    val users = getActiveUsers(yesterday, activityTensor)
    createUniformGroups(ctx, day, users)
  }

  private def assignPrune(ctx: OperatorContext[Row], day: Int, activityTensor: Array[Array[Boolean]]): Unit = {
    val yesterday = day - 1
    val users = getActiveUsers(yesterday, activityTensor)
    if (maxDelay > 0 && yesterday > maxDelay) {
      val minFreq = 1 / maxDelay.toDouble
      for (user <- users) {
        val freq = activityTensor.slice(0, yesterday).count(_.apply(user)).toDouble / yesterday
        if (freq < minFreq) {
          users.remove(user)
        }
      }
    }
    createUniformGroups(ctx, day, users)
  }

  private def getActiveUsers(day: Int, activityTensor: Array[Array[Boolean]]) = {
    val users = mutable.Set.empty[Int]
    activityTensor(day).zipWithIndex.foreach { case (active, user) =>
      if (active) {
        users += user
      }
    }
    users
  }

  private def createUniformGroups(ctx: OperatorContext[Row], day: Int, users: Iterable[Int]): Unit = {
    val groupsCount = math.ceil(users.size.toDouble / groupSize).toInt
    val rows = users.toSeq.sorted.map { user =>
      Row(user = user, day = day, group = user % groupsCount)
    }
    ctx.output.write(day.toString, rows)
  }


  private def readActivityTensor(): Array[Array[Boolean]] = {
    val result = Array.fill(daysCount + 1)(Array.fill[Boolean](usersCount)(false))
    forEach(0 to daysCount) { day =>
      activity.read(day.toString).foreach { row =>
        result(day)(row.user) = true
      }
    }
    result
  }
}

object AssignGroupsOperator {

  case class Row(user: Int, day: Int, group: Int)

}
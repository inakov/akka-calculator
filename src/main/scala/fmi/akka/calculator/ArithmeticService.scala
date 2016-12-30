package fmi.akka.calculator

import akka.actor.SupervisorStrategy.Stop
import akka.actor._
import fmi.akka.calculator.expression._

/**
 * Created by inakov on 5/21/15.
 */

object ArithmeticService{
  def props(): Props =
    Props(classOf[ArithmeticService])
}

class ArithmeticService extends Actor with ActorLogging {
  import ExpressionCalculator._

  private var timeJournal = Map[ActorRef, Long]()

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
    case e: ArithmeticException =>
      log.error("Evaluation failed - ArithmeticException: {}", e.getMessage)
      writeResult(System.currentTimeMillis, 0)
      timeJournal -= sender

      Stop
    case e =>
      log.error("Unexpected failure: {}", e.getMessage)
      timeJournal -= sender
      Stop
  }

  def receive = {
    case e: Tree =>
      val worker = context.actorOf(ExpressionCalculator.props(e))
      val startTime = System.currentTimeMillis
      timeJournal += worker -> startTime
    case Result(value) =>
      val startTime: Long = timeJournal(sender)
      timeJournal -= sender
      writeResult(startTime, value)
      if(timeJournal.isEmpty) self ! PoisonPill
  }

  private def writeResult(startTime: Long, result: BigInt) = {
    val endTime = System.currentTimeMillis - startTime
    log.info(s"Result: $result, Evaluation time in milliseconds: $endTime")
  }

}

package fmi.akka.calculator.expression.calculator

import java.io.File

import akka.actor.SupervisorStrategy.Stop
import akka.actor._
import fmi.akka.calculator.expression.Expression

/**
 * Created by inakov on 5/21/15.
 */
class ArithmeticService  extends Actor with ActorLogging {
  import ExpressionCalculator.{Result, Left}

  var pendingWorkers = Map[ActorRef, Long]()
  val outputFile = "/tmp/zad1-result.txt"

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = false) {
    case e: ArithmeticException =>
      log.error("Evaluation failed - ArithmeticException: {}", e.getMessage)
      pendingWorkers -= sender
      Stop
    case e =>
      log.error("Unexpected failure: {}", e.getMessage)
      pendingWorkers -= sender
      Stop
  }

  def receive = {
    case e: Expression =>
      val worker = context.actorOf(ExpressionCalculator.props(
        expr = e,
        position = Left)
      )
      val startTime = System.currentTimeMillis
      pendingWorkers += worker -> startTime

    case Result(originalExpression, value, _) =>
      val startTime: Long = pendingWorkers(sender)
      writeResult(originalExpression, value, startTime)
      pendingWorkers -= sender
      if(pendingWorkers.isEmpty) self ! PoisonPill
  }

  private def writeResult(originalExpression: Expression, result: Int, startTime: Long) = {
    val endTime = System.currentTimeMillis - startTime
    printToFile(new File(outputFile)) { p =>
      p.println(originalExpression)
      log.info(s"Result: $result, Evaluation time in milliseconds: $endTime")
      p.println(s"Result: $result, Evaluation time in milliseconds: $endTime")
    }
  }

  private def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try {
      op(p)
    } finally {
      p.close()
    }
  }

}
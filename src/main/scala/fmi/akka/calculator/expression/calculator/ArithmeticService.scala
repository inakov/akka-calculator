package fmi.akka.calculator.expression.calculator

import java.io.File

import akka.actor.SupervisorStrategy.Stop
import akka.actor._
import fmi.akka.calculator.expression.Expression

/**
 * Created by inakov on 5/21/15.
 */

object ArithmeticService{
  def props(outputFile: String): Props =
    Props(classOf[ArithmeticService], outputFile)
}

class ArithmeticService(outputFile: String)  extends Actor with ActorLogging {
  import ExpressionCalculator.{Result, Left}

  var pendingWorkers = Map[ActorRef, Long]()
  //val outputFile = "/tmp/zad1-result.txt"

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
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
      pendingWorkers -= sender
      writeResult(originalExpression, value, startTime)
      if(pendingWorkers.isEmpty) self ! PoisonPill
  }

  private def writeResult(originalExpression: Expression, result: BigInt, startTime: Long) = {
    val endTime = System.currentTimeMillis - startTime
    printToFile(new File(outputFile)) { p =>
      p.println(originalExpression)
//      log.info(s"Result: $result, Evaluation time in milliseconds: $endTime")
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
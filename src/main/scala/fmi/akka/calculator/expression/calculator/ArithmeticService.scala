package fmi.akka.calculator.expression.calculator

import akka.actor.SupervisorStrategy.Stop
import akka.actor._
import fmi.akka.calculator.expression.Expression

/**
 * Created by inakov on 5/21/15.
 */
class ArithmeticService  extends Actor with ActorLogging {
  import ExpressionCalculator.{Result, Left}

  var pendingWorkers = Map[ActorRef, ActorRef]()

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = false) {
    case e: ArithmeticException =>
      log.error("Evaluation failed - ArithmeticException: {}", e.getMessage)
      notifyConsumerFailure(worker = sender, failure = e)
      Stop
    case e =>
      log.error("Unexpected failure: {}", e.getMessage)
      notifyConsumerFailure(worker = sender, failure = e)
      Stop
  }

  def notifyConsumerFailure(worker: ActorRef, failure: Throwable): Unit = {
    pendingWorkers.get(worker) foreach { _ ! Status.Failure(failure) }
    pendingWorkers -= worker
  }

  def notifyConsumerSuccess(worker: ActorRef, result: Int): Unit = {
    pendingWorkers.get(worker) foreach { _ ! result }
    pendingWorkers -= worker
  }

  def receive = {
    case e: Expression =>
      val worker = context.actorOf(ExpressionCalculator.props(
        expr = e,
        position = Left)
      )
      pendingWorkers += worker -> sender

    case Result(originalExpression, value, _) =>
      notifyConsumerSuccess(worker = sender, result = value)
  }

}
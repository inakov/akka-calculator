package fmi.akka.calculator.expression.calculator

import akka.actor.SupervisorStrategy.Escalate
import akka.actor.{OneForOneStrategy, ActorLogging, Actor, Props}
import fmi.akka.calculator.expression.{Expression, Number, Add, Sub, Divide, Multiply}
import fmi.akka.calculator.expression.calculator.ExpressionCalculator.{Result, Position, Left, Right}

/**
 * Created by inakov on 5/21/15.
 */
object ExpressionCalculator {
  def props(expr: Expression, position: Position): Props =
    Props(classOf[ExpressionCalculator], expr, position)

  trait Position
  case object Left extends Position
  case object Right extends Position
  case class Result(originalExpression: Expression, value: Int, position: Position)
}

class ExpressionCalculator(
                            val expr: Expression,
                            val myPosition: Position)
  extends Actor with ActorLogging {

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = false) {
    case _ =>
      Escalate
  }
  var results  = Map.empty[Position, Int]
  var expected = Set[Position](Left, Right)

  override def preStart(): Unit = expr match {
    case Number(value) =>
      context.parent ! Result(expr, value, myPosition)
      context.stop(self)
    case _ =>
      context.actorOf(ExpressionCalculator.props(expr.left, Left),
        name = "left")
      context.actorOf(ExpressionCalculator.props(expr.right, Right),
        name = "right")
  }

  def receive = {
    case Result(_, value, position) if expected(position) =>
      expected -= position
      results += position -> value
      if (results.size == 2) {
        val result: Int = evaluate(expr, results(Left), results(Right))
//        log.info("Evaluated expression {} to value {}", expr, result)
        context.parent ! Result(expr, result, myPosition)
        context.stop(self)
      }
    case Result(_, _, position) =>
      throw new IllegalStateException(
        s"Expected results for positions ${expected.mkString(", ")} " +
          s"but got position $position"
      )
  }

  private def evaluate(expr: Expression, left: Int, right: Int): Int = expr match {
    case _: Add      => left + right
    case _: Sub      => left - right
    case _: Multiply => left * right
    case _: Divide   => left / right
  }

}


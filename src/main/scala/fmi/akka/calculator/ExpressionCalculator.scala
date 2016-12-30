package fmi.akka.calculator

import akka.actor.SupervisorStrategy.Escalate
import akka.actor.{Actor, ActorLogging, ActorRef, OneForOneStrategy, Props, SupervisorStrategy}
import fmi.akka.calculator.expression._

/**
 * Created by inakov on 5/21/15.
 */

object ExpressionCalculator {
  def props(expr: Tree): Props =
    Props(classOf[ExpressionCalculator], expr)

  case class Result(value: BigInt)

  sealed trait Position
  case object Left extends Position
  case object Right extends Position
}

class ExpressionCalculator(expr: Tree) extends Actor with ActorLogging {

  override val supervisorStrategy = OneForOneStrategy(loggingEnabled = true) {
    case _ => Escalate
  }

  import ExpressionCalculator._
  var resultLeft: Option[BigInt] = None
  var resultRight: Option[BigInt] = None
  var expected: Map[ActorRef, Position] = Map.empty[ActorRef, Position]

  override def preStart(): Unit = expr match {
    case Leaf(value) =>
      context.parent ! Result(value)
      context.stop(self)
    case Node(_, left, right) =>
      val l = context.actorOf(ExpressionCalculator.props(left))
      val r = context.actorOf(ExpressionCalculator.props(right))
      expected = Map(l -> Left, r -> Right)
  }

  def receive = {
    case Result(value) =>
      if(expected(sender()) == Left) resultLeft = Some(value)
      else resultRight = Some(value)

      if(resultLeft.isDefined && resultRight.isDefined){
        val resultValue = evaluate(resultLeft.get, resultRight.get)
        context.parent ! Result(resultValue)
        context.stop(self)
      }
    case _ => log.warning("Unexpected message!")
  }

  private def evaluate(left: BigInt, right: BigInt): BigInt = expr match {
    case Leaf(value) => value
    case Node(Add, _, _) => left + right
    case Node(Subtract, _, _) => left - right
    case Node(Multiply, _,_) => left * right
    case Node(Divide , _, _) => left / right
  }

}
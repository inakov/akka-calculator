/**
 * Created by inakov on 5/20/15.
 */
import akka.actor.{Props, ActorSystem}
import akka.pattern.ask
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import akka.util.Timeout

import fmi.akka.calculator.expression.{Add, Divide, Expression, Sub, Multiply, Number}
import fmi.akka.calculator.expression.calculator.ArithmeticService
import fmi.akka.calculator.expression.parser.MathematicalExpressionParser

object Main {

  def main(args: Array[String]) {
    val expressionString = "(3Num+5Num) / (2 * (1 + 1))";
    println("Expression: " + expressionString)

    val expression = MathematicalExpressionParser.parseAll(MathematicalExpressionParser.expr, expressionString)
    println(s"Parsed $expression")

    val system = ActorSystem("calculator-system")
    val calculatorService =
      system.actorOf(Props[ArithmeticService], "arithmetic-service")

    def calculate(expr: Expression): Future[Int] = {
      implicit val timeout = Timeout(1.second)
      (calculatorService ? expr).mapTo[Int]
    }

    val result = Await.result(calculate(expression.get), 1.second)
    println(s"Got result: $result")

    system.shutdown()
    system.awaitTermination()
  }
}

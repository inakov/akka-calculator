import akka.actor._
import fmi.akka.calculator.ArithmeticService
import fmi.akka.calculator.expression.parser.MathematicalExpressionParser

import scala.io.Source

/**
  * Created by inakov on 5/20/15.
  */
object Main extends App{

//  var expressionString = "((((3Num+5Num) / (2 * 1) - 2) * 6) + (((3Num+5Num) / (2 * 1) - 2) * 6)) / 6"
//  val expression = MathematicalExpressionParser.parseAll(MathematicalExpressionParser.expr, expressionString)
  import fmi.akka.calculator.random._

  val expression = trees.generate

  var system = ActorSystem("akka-calculator-system")

  val calculatorService =
    system.actorOf(ArithmeticService.props(), "arithmetic-service")

  system.actorOf(Props(classOf[Terminator], calculatorService), "terminator")

  println(expression)
  calculatorService ! expression

  private def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
    val p = new java.io.PrintWriter(f)
    try {
      op(p)
    } finally {
      p.close()
    }
  }

}

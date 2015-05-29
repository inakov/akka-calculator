/**
 * Created by inakov on 5/20/15.
 */
import akka.actor.{Props, ActorSystem}
import akka.pattern.ask
import com.typesafe.config.{ConfigValueFactory, ConfigValue, ConfigFactory}
import fmi.akka.calculator.expression.utils.actors.Reaper
import fmi.akka.calculator.expression.utils.actors.Reaper.WatchMe
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import akka.util.Timeout

import fmi.akka.calculator.expression._
import fmi.akka.calculator.expression.calculator.ArithmeticService
import fmi.akka.calculator.expression.parser.MathematicalExpressionParser

import scala.io.Source

object Main {

  def main(args: Array[String]) {


//    var expressionString = "(3Num+5Num) / (2 * (1 + 1))";
//    for(line <- Source.fromFile("/tmp/zad1-result.txt").getLines().take(1)){
//      expressionString = line;
//    }
//    println("Expression: " + expressionString)
//
//    val expression = MathematicalExpressionParser.parseAll(MathematicalExpressionParser.expr, expressionString)
//    println(s"Parsed $expression")
//
//    var testConfig = ConfigFactory.load().withValue("akka.actor.default-dispatcher.fork-join-executor.parallelism-max",ConfigValueFactory.fromAnyRef(1))
//    testConfig = testConfig.withValue("akka.actor.default-dispatcher.fork-join-executor.parallelism-min", ConfigValueFactory.fromAnyRef(1))
//    println(testConfig.getInt("akka.actor.default-dispatcher.fork-join-executor.parallelism-max"))
//    println(testConfig.getInt("akka.actor.default-dispatcher.fork-join-executor.parallelism-min"))


    val randomExpression = RandomExpressionGenerator.randomExpression(0.30, 40)
    println(s"Expression: $randomExpression")

    val system = ActorSystem("calculator-system")

    val reaper =
      system.actorOf(Props[Reaper], "reaper")

    val calculatorService =
      system.actorOf(Props[ArithmeticService], "arithmetic-service")
    reaper ! WatchMe(calculatorService)

    calculatorService ! randomExpression
  }
}

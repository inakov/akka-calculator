/**
 * Created by inakov on 5/20/15.
 */
import akka.actor._
import akka.pattern.ask
import com.typesafe.config.{Config, ConfigValueFactory, ConfigValue, ConfigFactory}
import fmi.akka.calculator.expression.utils.actors.Reaper
import fmi.akka.calculator.expression.utils.actors.Reaper.WatchMe
import fmi.akka.calculator.expression.calculator.ArithmeticService
import fmi.akka.calculator.expression.parser.MathematicalExpressionParser

import scala.io.Source

object Main {

  def main(args: Array[String]) {
    val arglist = args.toList
    type OptionMap = Map[Symbol, Any]
    def nextOption(map : OptionMap, list: List[String]) : OptionMap = {
      def isSwitch(s : String) = (s(0) == '-')
      list match {
        case Nil => map
        case "-f" :: value :: tail =>
          nextOption(map ++ Map('f -> value), tail)
        case "-o" :: value :: tail =>
          nextOption(map ++ Map('o -> value), tail)
        case "-t"  :: value :: tail =>
          nextOption(map ++ Map('t -> value.toInt), tail)
        case "-task"  :: value :: tail =>
          nextOption(map ++ Map('t -> value.toInt), tail)
        case "-q" :: tail  =>
          nextOption(map ++ Map('q -> true), Nil)
      }
    }
    val options = nextOption(Map(),arglist)
    println(options)

    if(options.get('f).isEmpty) System.exit(1)

    val inputFilePath = options.get('f).get.toString
    val outputFile = options.get('o).getOrElse("/tmp/result.txt")

    var expressionString = "((((3Num+5Num) / (2 * 1) - 2) * 6) + (((3Num+5Num) / (2 * 1) - 2) * 6)) / 6"
    for(line <- Source.fromFile(inputFilePath).getLines().take(1)){
      expressionString = line;
    }

    val expression = MathematicalExpressionParser.parseAll(MathematicalExpressionParser.expr, expressionString)

    var system = ActorSystem("akka-calculator-system")
    if(options.get('t).isDefined){
      val numThreads = (options.get('t) match {
        case Some(x:Int) => x
        case _ => Int.MinValue
      })
      var testConfig = buildConfiguration(numThreads)
      system = ActorSystem("calculator-system", testConfig)
    }

    val calculatorService =
      system.actorOf(ArithmeticService.props(outputFile.toString), "arithmetic-service")
    system.actorOf(Props(classOf[Terminator], calculatorService), "terminator")

    calculatorService ! expression.get

  }

  def buildConfiguration(numberOfThreads: Int): Config ={
    val testConfig = ConfigFactory.load().withValue("akka.actor.default-dispatcher.fork-join-executor.parallelism-max",ConfigValueFactory.fromAnyRef(numberOfThreads))
    testConfig.withValue("akka.actor.default-dispatcher.fork-join-executor.parallelism-min", ConfigValueFactory.fromAnyRef(1))
  }

  class Terminator(ref: ActorRef) extends Actor with ActorLogging {
    context watch ref
    def receive = {
      case Terminated(_) =>
        context.system.terminate()
    }
  }

}

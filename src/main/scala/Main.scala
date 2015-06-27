/**
 * Created by inakov on 5/20/15.
 */
import akka.actor.{Props, ActorSystem}
import akka.pattern.ask
import com.typesafe.config.{Config, ConfigValueFactory, ConfigValue, ConfigFactory}
import fmi.akka.calculator.expression.utils.actors.Reaper
import fmi.akka.calculator.expression.utils.actors.Reaper.WatchMe
import fmi.akka.calculator.expression.calculator.ArithmeticService
import fmi.akka.calculator.expression.parser.MathematicalExpressionParser

object Main {



  def main(args: Array[String]) {

//    val arglist = args.toList
//    type OptionMap = Map[Symbol, Any]
//    def nextOption(map : OptionMap, list: List[String]) : OptionMap = {
//      def isSwitch(s : String) = (s(0) == '-')
//      list match {
//        case Nil => map
//        case "-f" :: value :: tail =>
//          nextOption(map ++ Map('f -> value), tail)
//        case "-o" :: value :: tail =>
//          nextOption(map ++ Map('o -> value), tail)
//        case "-t"  :: value :: tail =>
//          nextOption(map ++ Map('t -> value.toInt), tail)
//        case "-task"  :: value :: tail =>
//          nextOption(map ++ Map('t -> value.toInt), tail)
//        case "-q" :: tail  =>
//          nextOption(map ++ Map('q -> true), Nil)
//      }
//    }
//    val options = nextOption(Map(),arglist)
////    println(options)
//
//
//    if(options.get('f).isEmpty) System.exit(1)
//
//    val inputFilePath = options.get('f).get.toString
//    val outputFile = options.get('o).getOrElse("/tmp/result.txt")
//    println(inputFilePath)
//    println(outputFile)

    var expressionString = "(3Num+5Num) / (2 * (1 + 1))"
//    for(line <- Source.fromFile("/tmp/zad1-result.txt").getLines().take(1)){
//      expressionString = line;
//    }
    println("Expression: " + expressionString)

    val expression = MathematicalExpressionParser.parseAll(MathematicalExpressionParser.expr, expressionString)
    println(s"Parsed $expression")

    var system = ActorSystem("akka-calculator-system")
//    if(options.get('t).isDefined){
//      val numThreads = (options.get('t) match {
//        case Some(x:Int) => x
//        case _ => Int.MinValue
//      })
//      var testConfig = buildConfiguration(numThreads)
//      system = ActorSystem("calculator-system", testConfig)
//    }

    val reaper =
      system.actorOf(Props[Reaper], "reaper")

    val calculatorService =
      system.actorOf(Props[ArithmeticService], "arithmetic-service")
    reaper ! WatchMe(calculatorService)

    calculatorService ! expression


//    val randomExpression = RandomExpressionGenerator.randomExpression(0.30, 40)
//    println(s"Expression: $randomExpression")
//

  }

//  def buildConfiguration(numberOfThreads: Int): Config ={
//    var testConfig = ConfigFactory.load().withValue("akka.actor.default-dispatcher.fork-join-executor.parallelism-max",ConfigValueFactory.fromAnyRef(numberOfThreads))
//    testConfig = testConfig.withValue("akka.actor.default-dispatcher.fork-join-executor.parallelism-min", ConfigValueFactory.fromAnyRef(1))
//
//    testConfig
//  }


}

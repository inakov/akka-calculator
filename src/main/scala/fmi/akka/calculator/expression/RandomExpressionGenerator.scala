package fmi.akka.calculator.expression

import scala.util.Random

/**
 * Created by inakov on 5/26/15.
 */
object RandomExpressionGenerator {

  def randomExpression(numberProb: Double, maxDepth: Int): Expression = {

    if(maxDepth == 0 || Random.nextDouble() < numberProb) return Number(Random.nextInt(100))

    val LHS = randomExpression(numberProb, maxDepth - 1)
    val RHS = randomExpression(numberProb, maxDepth - 1)

    def randomOperation(left: Expression, right: Expression): Expression = {
      val randomNumber = Random.nextInt(3)
      randomNumber match {
        case 0 => Add(left, right)
        case 1 => Sub(left, right)
        case 2 => Multiply(left, right)
        case 3 => Divide(left, right)
      }
    }

    randomOperation(LHS, RHS)
  }

}

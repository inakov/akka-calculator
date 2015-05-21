package fmi.akka.calculator.expression.parser

import fmi.akka.calculator.expression.{Multiply, Add, Number, Expression, Sub, Divide}

import scala.util.parsing.combinator._


/**
 * Created by inakov on 5/21/15.
 */

//Grammar
//Num ­> 0 | ... | 9 | 0Num | 1Num | ... | 9Num
//Expr ­> Expr + Term | Expr ­ Term | Term
//Term ­> Term * Factor | Term / Factor | Factor
//Factor ­> Num | (Expr)


object MathematicalExpressionParser  extends JavaTokenParsers {
  def num: Parser[Expression] = (wholeNumber <~ "Num" ^^ (x => Number(x.toInt))) | wholeNumber ^^ (x => Number(x.toInt))

  def expr: Parser[Expression] = term ~ rep(("+" | "-") ~ term) ^^ {
    case trm~lst => lst.foldLeft(trm){(acc, t) => if(t._1 == "+") Add(acc, t._2) else Sub(acc, t._2)}
  }
  def term: Parser[Expression] = factor ~ rep(("*" | "/") ~ factor) ^^ {
    case fac~lst => lst.foldLeft(fac){(acc, t) => if(t._1 == "*") Multiply(acc, t._2) else Divide(acc, t._2)}
  }
  def factor: Parser[Expression] = num | "(" ~> expr <~")" ^^ (x => x)

}

package fmi.akka.calculator.expression.parser

import fmi.akka.calculator.expression._
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
  def num: Parser[Tree] = (wholeNumber <~ "Num" ^^ (x => Leaf(x.toInt))) | wholeNumber ^^ (x => Leaf(x.toInt))

  def expr: Parser[Tree] = term ~ rep(("+" | "-") ~ term) ^^ {
    case trm~lst => lst.foldLeft(trm){(acc, t) =>
      if(t._1 == "+") Node(Add, acc, t._2) else Node(Subtract, acc, t._2)
    }
  }

  def term: Parser[Tree] = factor ~ rep(("*" | "/") ~ factor) ^^ {
    case fac~lst => lst.foldLeft(fac){(acc, t) =>
      if(t._1 == "*") Node(Multiply, acc, t._2) else Node(Divide, acc, t._2)
    }
  }

  def factor: Parser[Tree] = num | "(" ~> expr <~")" ^^ (x => x)

}

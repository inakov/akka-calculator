package fmi.akka.calculator.expression

/**
 * Created by inakov on 5/21/15.
 */
trait Expression {
  def left: Expression
  def right: Expression
}

case class Add(left: Expression, right: Expression) extends Expression {
  override val toString = s"($left + $right)"
}

case class Sub(left: Expression, right: Expression) extends Expression {
  override val toString = s"($left - $right)"
}

case class Multiply(left: Expression, right: Expression) extends Expression {
  override val toString = s"($left * $right)"
}

case class Divide(left: Expression, right: Expression) extends Expression {
  override val toString = s"($left / $right)"
}

case class Number(value: BigInt) extends Expression {
  def left = this
  def right = this
  override val toString = String.valueOf(value)
}

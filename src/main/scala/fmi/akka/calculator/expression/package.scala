package fmi.akka.calculator

/**
  * Created by inakov on 5/21/15.
  */
package object expression {

  sealed trait Tree
  case class Leaf(value: Int) extends Tree{
    override def toString: String = value.toString
  }
  case class Node(op: Operation, left: Tree, right: Tree) extends Tree{
    override def toString: String = "(" + left + op + right + ")"
  }

  sealed trait Operation
  case object Add extends Operation {
    override def toString: String = "+"
  }

  case object Subtract extends Operation{
    override def toString: String = "-"
  }

  case object Divide extends Operation {
    override def toString: String = "/"
  }

  case object Multiply extends Operation {
    override def toString: String = "*"
  }

}

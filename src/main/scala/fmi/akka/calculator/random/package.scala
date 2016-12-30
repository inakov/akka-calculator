package fmi.akka.calculator

import scala.util.Random

/**
  * Created by inakov on 30.12.16.
  */
package object random {

  trait Generator[+T] {
    self =>

    def generate: T

    def map[S](f: T => S): Generator[S] = new Generator[S] {
      override def generate = f (self.generate)
    }

    def flatMap[S](f: T => Generator[S]): Generator[S] = new Generator[S] {
      override def generate: S = f(self.generate).generate
    }
  }

  val integers = new Generator[Int] {
    override def generate: Int = Random.nextInt(100)
  }

  def choose(lo: Int, hi: Int): Generator[Int] = {
    for(x <- integers) yield lo + (x % (hi - lo))
  }

  def oneOf[T](xs: T*) = {
    for(idx <- choose(0, xs.length)) yield xs(idx)
  }

  val coin: Generator[Boolean] = for(x <- integers) yield x < 50

  import expression._

  def leaf: Generator[Leaf] = for(x <- integers) yield Leaf(x)

  val operations: Generator[Operation] = for(op <- oneOf(Add, Subtract, Divide, Multiply)) yield op

  def node: Generator[Node] = for {
    op <- operations
    left <- trees
    right <- trees
  } yield Node(op, left, right)

  def trees: Generator[Tree] = for{
    isLeaf <- coin
    tree <- if(isLeaf) leaf else node
  } yield tree

}

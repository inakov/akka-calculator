package fmi.akka.calculator.expression.utils.actors

import akka.actor.{Terminated, Actor, ActorRef}

import scala.collection.mutable.ArrayBuffer

/**
 * Created by inakov on 5/26/15.
 */
object Reaper {
  case class WatchMe(ref: ActorRef)
}

class Reaper extends Actor {
  import Reaper._

  val watched = ArrayBuffer.empty[ActorRef]

  def allSoulsReaped(): Unit = context.system.terminate

  final def receive = {
    case WatchMe(ref) =>
      context.watch(ref)
      watched += ref
    case Terminated(ref) =>
      watched -= ref
      if (watched.isEmpty) allSoulsReaped()
  }
}
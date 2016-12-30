import akka.actor.{Actor, ActorLogging, ActorRef, Terminated}

/**
  * Created by inakov on 30.12.16.
  */
class Terminator(ref: ActorRef) extends Actor with ActorLogging {
  context watch ref

  def receive = {
    case Terminated(_) =>
      context.system.terminate()
  }

}

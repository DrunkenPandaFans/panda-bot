package sk.drunkenpanda.bot.actor

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.Props
import sk.drunkenpanda.bot.PrivateMessage
import sk.drunkenpanda.bot.Ping
import sk.drunkenpanda.bot.Pong
import sk.drunkenpanda.bot.Response

object MasterActor {
  case object Start
  case object Stop

  def props(processor: ActorRef, reader: ActorRef, writer: ActorRef) =
    Props(classOf[MasterActor], processor, reader, writer)
}

class MasterActor(processor: ActorRef, reader: ActorRef,
  writer: ActorRef) extends Actor {
    
    def receive = {
      case MasterActor.Start => reader ! StreamReader.Start
      case m: PrivateMessage => processor ! m
      case m: Ping => processor ! m
      case m: Pong => writer ! m
      case m: Response => writer ! m
    }

}


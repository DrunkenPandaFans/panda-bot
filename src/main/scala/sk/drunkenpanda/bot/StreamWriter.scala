package sk.drunkenpanda.bot

import akka.actor.Actor
import akka.actor.Props
import sk.drunkenpanda.bot.io.ConnectionSource

object StreamWriter {

  def props(bot: Bot, source: ConnectionSource) = 
    Props(classOf[StreamWriter], bot, source)

}

class StreamWriter(bot: Bot, source: ConnectionSource) extends Actor 
  with LoggableActor {

  def receive = {
    case m:Response => bot.send(m)(source)
    case pong: Pong => bot.send(pong)(source)
    case m: AnyRef => log.info(s"It is not possible to send $m")
  }
}

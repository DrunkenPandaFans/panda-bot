package sk.drunkenpanda.bot

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import sk.drunkenpanda.bot.io.ConnectionSource

object StreamReader {
  case object Start
  case object Stop

  def props(bot: Bot, source: ConnectionSource) = 
    Props(classOf[StreamReader], bot, source)
}

class StreamReader(bot: Bot, source: ConnectionSource) 
  extends Actor with LoggableActor {    

  def receive = {
    case StreamReader.Start => start
    case m: Any => log.info(s"ChannelActor received unrecognized message: $m")
  }

  def start = {
    bot.connect("PandaBot", "Drunken Panda", "#drunken-panda")(source)
    val messageStream = bot.listen()(source)
    messageStream foreach { msg =>  sender ! msg }
  }

}

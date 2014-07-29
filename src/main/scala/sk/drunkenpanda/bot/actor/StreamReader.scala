package sk.drunkenpanda.bot.actor

import akka.actor.Actor
import akka.actor.Props
import akka.event.Logging
import sk.drunkenpanda.bot.{Config, Bot}
import sk.drunkenpanda.bot.io.ConnectionSource

object StreamReader {
  case object Start
  case object Stop

  def props(bot: Bot, source: ConnectionSource, config: Config) =
    Props(classOf[StreamReader], bot, source, config)
}

class StreamReader(bot: Bot, source: ConnectionSource, config: Config)
  extends Actor with LoggableActor {    

  def receive = {
    case StreamReader.Start => start
    case m: Any => log.info(s"ChannelActor received unrecognized message: $m")
  }

  def start = {
    bot.connect(config.nickname, config.realname, config.channel)(source)
    val messageStream = bot.listen()(source)
    messageStream foreach { msg =>  sender ! msg }
  }

}

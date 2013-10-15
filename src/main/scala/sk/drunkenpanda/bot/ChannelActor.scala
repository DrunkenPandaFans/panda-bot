package sk.drunkenpanda.bot

import akka.actor.Actor
import akka.event.Logging
import sk.drunkenpanda.bot.io.ConnectionSource

object ChannelActor {
  case object Start
  case object Stop
}

class ChannelActor(bot: Bot, source: ConnectionSource) 
  extends Actor with LoggableActor {    

  def receive = {
    case ChannelActor.Start => startBot
    case ChannelActor.Stop => shutdownBot
    case m: Message => sendMessage(m)
    case m: Any => log.info(s"ChannelActor received unrecognized message: #{m}")
  }

  def startBot = {
    bot.connect("PandaBot", "Drunken Panda Bot", "#drunken-panda")(source)
    val messageStream = bot.listen()(source)
    messageStream map { msg => sender ! msg }
  }

  def shutdownBot = bot.leave("#drunken-panda")(source)

  def sendMessage(m: Message) = bot.send(m)(source)

}

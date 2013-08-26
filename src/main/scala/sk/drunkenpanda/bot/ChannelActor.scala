package sk.drunkenpanda.bot

import akka.actor._

case object Start
case object Stop

/*class ChannelActor(bot: Bot, source: ConnectionSource[Socket]) extends Actor {    

  def receive = {
    case Start => startBot
    case Stop => shutdownBot
    case m: Message => sendMessage(m)
    case _ => //log weird message
  }

  def startBot = {
    bot.connect("PandaBot", "Drunken Panda Bot", "#drunken-panda")(source)
    val messageStream = bot.listen()(source)
    messageStream map { msg => sender ! msg }
  }

  def shutdownBot = {
    bot.leave("#drunken-panda")(source)
    source.close
  }

  def sendMessage(m: Message) = bot.send(m)(source)

} */

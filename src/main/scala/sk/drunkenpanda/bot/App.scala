package sk.drunkenpanda.bot

import akka.actor.Props
import java.net.Socket
import util.Properties

import sk.drunkenpanda.bot.io._
import sk.drunkenpanda.bot.plugins.AbstractPluginModule
import sk.drunkenpanda.bot.plugins.EchoPlugin
import sk.drunkenpanda.bot.plugins.PongPlugin

object App {

  val source = new SocketConnectionSource(new Socket("irc.freenode.net", 6667))

  val bot = new Bot(new NetworkIrcClient())

  def startBot(source: ConnectionSource) = {
    val channelProps = ChannelActor.props(bot, source)
    val processProps = ProcessingActor.props(new SimplePluginModule)

    val system = ActorSystem()
    val channelActor = system.actorOf(channelProps)
    val processActor = system.actorOf(processActor)
    channelActor ! ChannelActor.Start
  }
  
  def main(args: Array[String]): Unit = startBot(source)
  
}

class SimplePluginModule extends AbstractPluginModule {

  override val plugins = Set(new PongPlugin(), new EchoPlugin())
}

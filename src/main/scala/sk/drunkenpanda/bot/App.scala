  package sk.drunkenpanda.bot

import akka.actor.ActorSystem
import java.net.Socket
import sk.drunkenpanda.bot.actor._
import sk.drunkenpanda.bot.io._
import sk.drunkenpanda.bot.plugins.AbstractPluginModule
import sk.drunkenpanda.bot.plugins.EchoPlugin
import sk.drunkenpanda.bot.plugins.PongPlugin
import sk.drunkenpanda.bot.plugins.calc.{Calculator, ExpressionParser, CalculatorPlugin}

object App {

  val source = new SocketConnectionSource(new Socket("irc.freenode.net", 6667))

  val bot = new Bot(new NetworkIrcClient())

  def startBot(source: ConnectionSource) = {
    val system = ActorSystem("pandabot")
    val config = Config.fromProperties("config.props")
    val writer = system.actorOf(StreamWriter.props(bot, source))
    val reader = system.actorOf(StreamReader.props(bot, source, config))
    val processor = system.actorOf(MessageProcessor.props(new SimplePluginModule))
    val masterActor = system.actorOf(
      MasterActor.props(processor, reader, writer))
    masterActor ! MasterActor.Start
  }
  
  def main(args: Array[String]): Unit = startBot(source)

 
  class SimplePluginModule extends AbstractPluginModule {

    override val plugins = Set(new PongPlugin(), new EchoPlugin(),
      new CalculatorPlugin(new Calculator, new ExpressionParser))
  } 
}



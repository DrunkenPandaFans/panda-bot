package sk.drunkenpanda.bot

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.actor.Props
import java.net.Socket
import sk.drunkenpanda.bot.io._
import sk.drunkenpanda.bot.plugins.AbstractPluginModule
import sk.drunkenpanda.bot.plugins.EchoPlugin
import sk.drunkenpanda.bot.plugins.PongPlugin

object App {

  val source = new SocketConnectionSource(new Socket("irc.freenode.net", 6667))

  val bot = new Bot(new NetworkIrcClient())

  def startBot(source: ConnectionSource) = {
    val system = ActorSystem("pandabot")
    val writer = system.actorOf(StreamWriter.props(bot, source))
    val reader = system.actorOf(StreamReader.props(bot, source))
    val processor = system.actorOf(MessageProcessor.props(new SimplePluginModule))
    val masterActor = system.actorOf(Props(classOf[MasterActor], 
      processor, reader, writer))
    masterActor ! StreamReader.Start
  }
  
  def main(args: Array[String]): Unit = startBot(source)

  class MasterActor(processor: ActorRef, reader: ActorRef, writer: ActorRef) 
    extends Actor {
    
    def receive = {
      case StreamReader.Start => reader ! StreamReader.Start
      case m: PrivateMessage => processor ! m
      case m: Ping => processor ! m
      case m: Pong => writer ! m
      case m: Response => writer ! m
    }

}
 
  class SimplePluginModule extends AbstractPluginModule {

    override val plugins = Set(new PongPlugin(), new EchoPlugin())
  } 
}



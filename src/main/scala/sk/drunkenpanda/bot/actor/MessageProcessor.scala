package sk.drunkenpanda.bot.actor

import akka.actor.Actor
import akka.actor.Props
import sk.drunkenpanda.bot.Message
import sk.drunkenpanda.bot.plugins.PluginModule

object MessageProcessor {

  def props(module: PluginModule): Props = Props(classOf[MessageProcessor], module)

}

class MessageProcessor(pluginRepo: PluginModule) extends Actor with LoggableActor {

  def receive = {
    case m: Message => pluginRepo.process(m).foreach {respond => sender ! respond}
  }
}

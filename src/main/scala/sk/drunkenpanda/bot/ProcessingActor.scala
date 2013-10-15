package sk.drunkenpanda.bot

import sk.drunkenpanda.bot.plugins.PluginModule
import akka.actor.Actor

class ProcessingActor(pluginRepo: PluginModule) extends Actor with LoggableActor {

  def receive = {
    case m: Message => pluginRepo.process(m).foreach {respond => sender ! respond}
  }
}

package sk.drunkenpanda.bot

import sk.drunkenpanda.bot.plugins.PluginModule
import akka.actor.Actor
import akka.actor.Props

object ProcessingActor {

  def props(module: PluginModule): Props = Props(classOf[ProcessingActor], module)

}

class ProcessingActor(pluginRepo: PluginModule) extends Actor with LoggableActor {

  def receive = {
    case m: Message => pluginRepo.process(m).foreach {respond => sender ! respond}
  }
}

package sk.drunkenpanda.bot

import akka.actor.Actor

class ProcessingActor(pluginRepo: PluginRepository) extends Actor with LoggableActor {

  def receive = {
    case m: Message = pluginRepo.process(m).foreach {respond => sender ! respond}
  }
}

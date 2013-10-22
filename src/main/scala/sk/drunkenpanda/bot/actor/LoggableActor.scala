package sk.drunkenpanda.bot.actor

import akka.actor.Actor
import akka.event.Logging

trait LoggableActor extends Actor {
  val log = Logging(context.system, this)
}

package sk.drunkenpanda.bot.plugins

import sk.drunkenpanda.bot.Message
import sk.drunkenpanda.bot.PrivateMessage
import sk.drunkenpanda.bot.Ping
import sk.drunkenpanda.bot.PrivateMessage


trait Plugin {
  def respond(message: Message): Option[Message]
}

class EchoPlugin extends Plugin {

  override def respond(message: Message) = message match {
    case PrivateMessage(from, text) => 
      Option(new PrivateMessage(from, "Echoing message..." + text))
    case _ => None
  }
}

class PongPlugin extends Plugin {

  override def respond(message: Message) = message match {
    case Ping(hash) => Option(new Pong(hash))
    case _ => None
  }
}

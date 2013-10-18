package sk.drunkenpanda.bot.plugins

import sk.drunkenpanda.bot.Message
import sk.drunkenpanda.bot.Ping
import sk.drunkenpanda.bot.PrivateMessage
import sk.drunkenpanda.bot.Pong


trait Plugin {
  def respond(message: Message): Option[Message]
}

class EchoPlugin extends Plugin {

  private lazy val format = """panda echo (.+), please\.*""".r

  override def respond(message: Message) = message match {
    case PrivateMessage(from, text) => prepareResponse(from, parseText(text))
    case _ => None
  }

  def prepareResponse(to: String, responseMessage: Option[String]) = 
    for {
      message <- responseMessage
    } yield new PrivateMessage(to, s"Echoing message...$message")

  def parseText(text: String) = text match {
    case format(toEcho) => Some(toEcho)
    case _ => None
  }
}

class PongPlugin extends Plugin {

  override def respond(message: Message) = message match {
    case Ping(hash) => Option(new Pong(hash))
    case _ => None
  }
}

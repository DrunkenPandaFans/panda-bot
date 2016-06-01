package sk.drunkenpanda.bot.plugins

import sk.drunkenpanda.bot.Message
import sk.drunkenpanda.bot.Ping
import sk.drunkenpanda.bot.Pong
import sk.drunkenpanda.bot.PrivateMessage
import sk.drunkenpanda.bot.Response


trait Plugin {
  def respond(message: Message): Option[Message]

  def onShutdown(): Unit
}

class EchoPlugin extends Plugin {

  private lazy val format = "panda echo (.+?)([\\.\\!\\?]+)?".r

  private lazy val echoCount = 3

  override def respond(message: Message): Option[Message] = message match {
    case PrivateMessage(from, text) => for {
        (msg, suffix) <- parseText(text)
      } yield prepareResponse(from, msg, suffix)
    case _ => None
  }

  def prepareResponse(to: String, message: String, suffix: String): Message = {
    val echo = List.fill(echoCount)(message.split(" ").last).mkString(" ")
    Response(to, s"$message $echo$suffix")
  }

  def parseText(text: String): Option[(String, String)] = text match {
    case format(toEcho, null) => Some((toEcho, ""))
    case format(toEcho, suffix) => Some((toEcho, suffix))
    case _ => None
  }

  override def onShutdown(): Unit = ()
}

class PongPlugin extends Plugin {

  override def respond(message: Message) = message match {
    case Ping(hash) => Option(new Pong(hash))
    case _ => None
  }

  override def onShutdown() = ()
}

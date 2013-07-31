package sk.drunkenpanda.bot.plugins

trait Plugin {
  def respond(msg: String): Option[String]
}

class EchoPlugin extends Plugin {

  def respond(msg: String) = Option("Echoing message..." + msg)
}

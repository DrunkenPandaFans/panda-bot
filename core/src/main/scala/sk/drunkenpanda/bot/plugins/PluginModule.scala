package sk.drunkenpanda.bot.plugins

import sk.drunkenpanda.bot.Message

trait PluginModule {
  def plugins: Set[Plugin]

  def process(message: Message): Set[Message]

  def shutdown(): Unit
}

abstract class AbstractPluginModule extends PluginModule {

  def process(message: Message): Set[Message] =
    plugins flatMap (_.respond(message))

  def shutdown() = plugins.foreach(_.onShutdown)
}

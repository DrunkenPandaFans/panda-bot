package sk.drunkenpanda.bot

import rx.lang.scala.Subscription
import sk.drunkenpanda.bot.io._
import sk.drunkenpanda.bot.plugins.PluginModule

class Bot(ircClient: IrcClient, pluginModule: PluginModule) {

  def start(username: String, nickname: String, realName: String, channels: Seq[String]): Subscription = {
    // check and handle errors
    ircClient.connect(username, nickname, realName)
    ircClient.listen(channels).filter(!_.isEmpty)
      .map(s => Message.parse(s))
      .map(pluginModule.process(_))
      .subscribe(
        responseMessages => {
          println("Incoming Message: " + responseMessages)
          responseMessages.foreach(ircClient.write(_))
        },
        err => {
          println("Error: " + err.getMessage)
        },
        () => stop
      )
  }

  def stop(): Unit = {
    ircClient.shutdown
    pluginModule.shutdown
  }
}

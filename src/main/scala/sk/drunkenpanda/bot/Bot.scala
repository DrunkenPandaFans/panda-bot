package sk.drunkenpanda.bot

import sk.drunkenpanda.bot.io._
import sk.drunkenpanda.bot.plugins.PluginModule

class Bot(ircClient: IrcClient, pluginModule: PluginModule) {

  def start(username: String, nickname: String, realName: String, channels: Seq[String]): Unit = {
    // check and handle errors
    ircClient.connect(username, nickname, realName)
    val incomingMessages = ircClient.listen(channels)
    incomingMessages.filter(!_.isEmpty)
      .map(s => {println(s); Message.parse(s)})
      .map(pluginModule.process(_))
      .subscribe(
        responseMessages => responseMessages.foreach(ircClient.write(_)),
        err => println("Error: " + err.getMessage))
  }

  def stop: Unit = {
    ircClient.shutdown
    pluginModule.shutdown
  }
}

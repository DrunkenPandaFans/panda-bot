package sk.drunkenpanda.bot

import java.net.Socket
import sk.drunkenpanda.bot.io._
import sk.drunkenpanda.bot.plugins.PluginModule

class Bot(client: IrcClient) {

  def connect(nickname: String, realname: String, channel: String)
    : ConnectionSource => Unit =
        s => {
          client.open(realname, nickname)(s)
          client.join(channel)(s)
        }
      
  def send(message: Message): ConnectionSource => Unit =
    s => client.send(message)(s)

  def listen(): ConnectionSource => Stream[Message] = 
    s => read(s)
   
  def leave(channel: String): ConnectionSource => Unit =
    s => client.leave(channel)(s)

  private def read(source: ConnectionSource): Stream[Message] = 
    client.receive()(source) #:: read(source)
      
}

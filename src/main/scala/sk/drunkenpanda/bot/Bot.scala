package sk.drunkenpanda.bot

import java.net.Socket
import sk.drunkenpanda.bot.io._
import sk.drunkenpanda.bot.plugins._

class Bot(client: IrcClient) {

  val plugins = List(new EchoPlugin(), new PongPlugin())

  def connect(nickname: String, realname: String, channel: String)
    : ConnectionSource => Unit =
        s => {
          client.open(realname, nickname)(s)
          client.join(channel)(s)
        }
   
  def process(message: Message): List[Message] = 
    for {    
      p <- plugins
      r <- p respond message
    } yield r
    
  def send(messages: List[Message]): ConnectionSource => Unit =
    s => messages map {message => client.send(message)(s)}

  def listen(): ConnectionSource => Stream[Message] = 
    s => read(s)
  
  def processStream(messages: Stream[Message])
      : ConnectionSource => Stream[List[Message]] = 
    s => messages map {msg => process(msg)}

  private def read(source: ConnectionSource): Stream[Message] = 
    client.receive()(source) #:: read(source)
      
}

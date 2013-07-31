package sk.drunkenpanda.bot

import sk.drunkenpanda.bot.plugins._
import java.net.Socket

class Bot(hostname: String, port: Int) {

  lazy val socket = 
    new SocketConnectionSource(new Socket(hostname, port))

  val client = new NetworkIrcClient()

  val plugins = List(new EchoPlugin())

  def connect(nickname: String, realname: String, channel: String): Unit = {
    client.open(realname, nickname)(socket)
    client.join(channel)(socket)

    val messageStream = listen()
    val results = for {
      msg <- messageStream
      result <- process(msg) 
    } yield result
    
    results.foreach (r => client.send(channel, r)(socket))
  }

  def process(message: String) = for {
    p <- plugins
    r <- p respond message
  } yield r

  def listen(): Stream[String] = 
    client.receive()(socket) #:: listen()

}

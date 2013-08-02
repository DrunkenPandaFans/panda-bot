package sk.drunkenpanda.bot

import java.net.Socket
import sk.drunkenpanda.bot.io._
import sk.drunkenpanda.bot.plugins._

class Bot(client: IrcClient) {

  val plugins = List(new EchoPlugin(), new PongPlugin())

  def connect(nickname: String, realname: String, channel: String) = 
    for {
     _ <- client.open(realname, nickname)
     _ <- client.join(channel)
  } yield ()
   
  def process(message: Message): List[Message] = 
    for {    
      p <- plugins
      r <- p respond message
    } yield r

//  def listen(): Action[ConnectionSource, Unit] = 
//    for {
//      message <- client.receive
//      respond <- process(message)
//      _ <- client.send(respond)
//    } yield ()
   
}

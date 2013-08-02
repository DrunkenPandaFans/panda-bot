package sk.drunkenpanda.bot

import java.net.Socket
import sk.drunkenpanda.bot.io._
import sk.drunkenpanda.bot.plugins._

class Bot(client: IrcClient) {

  val plugins = List(new EchoPlugin(), new PongPlugin())

  def connect(nickname: String, realname: String, channel: String): Action[ConnectionSource, Unit] = 
    for {
     _ <- client.open(realname, nickname)     
     _ <- client.join(channel)
  } yield ()
   
  def process(message: Message): List[Message] = 
    for {    
      p <- plugins
      r <- p respond message
    } yield r
    
  def send(messages: List[Message]): Action[ConnectionSource, Unit] = 
    new Action(source => messages map {message => client.send(message)})

  def listen(): Action[ConnectionSource, Stream[Message]] = 
    new Action(source => read(source))
  
  def processAction(messages: Stream[Message]): Action[ConnectionSource, Stream[Message]] = 
    new Action(source => messages flatMap process)
   
  private def read(source: ConnectionSource): Stream[Message] = 
    client.receive()(source) #:: read(source)
   
}

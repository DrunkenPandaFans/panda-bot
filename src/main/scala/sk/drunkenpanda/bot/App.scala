package sk.drunkenpanda.bot

import java.net.Socket
import sk.drunkenpanda.bot.io._

object App {

  val source = new SocketConnectionSource(new Socket("irc.freenode.net", 6667))

  val bot = new Bot(new NetworkIrcClient())

  def run(source: ConnectionSource) =
  { 
    for {
      _ <- Action.pure(println("Connecting to channel"))
      _ <- bot.connect("drunken-panda", "Drunken Panda Bot", "#drunken-panda")      
      messages <- bot.listen()
      _ <- Action.pure(println(messages))
      process <- bot.processAction(messages)
      _ <- bot.send(process.toList)
    } yield ()  
  }

  def main(args: Array[String]): Unit = run(source) 
  
}

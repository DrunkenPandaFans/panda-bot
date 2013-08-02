package sk.drunkenpanda.bot

import java.net.Socket
import sk.drunkenpanda.bot.io._

object App {

  lazy val source = new SocketConnectionSource(new Socket("irc.freenode.net", 6667))

  val bot = new Bot(new NetworkIrcClient())

  def run() = 
//    for {
      bot.connect("drunken-panda", "Drunken Panda Bot", "#drunken-panda")
//      response <- bot.listen
//    } yield response

  def main(args: Array[String]): Unit = run()(source) 
  
}

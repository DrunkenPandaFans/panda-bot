package sk.drunkenpanda.bot

import java.net.Socket
import sk.drunkenpanda.bot.io._

object App {

  val source = new SocketConnectionSource(new Socket("irc.freenode.net", 6667))

  val bot = new Bot(new NetworkIrcClient())

  def run(source: ConnectionSource) = {
    bot.connect("PandaBot", "Drunken Panda Bot", "#drunken-panda")(source)

    val messageStream = bot.listen()(source)
    val responds = messageStream map { msg => bot.process(msg) }
    responds foreach { r => bot.send(r)(source) }
  }
   
   

  def main(args: Array[String]): Unit = run(source) 
  
}

package sk.drunkenpanda.bot

import java.net.Socket
import scala.util.control.Breaks._

object App {

  val bot = new NetworkIrcClient()

  def main(args: Array[String]): Unit = {
    val socket = new Socket("irc.freenode.net", 6667)
    val connectionSource = new SocketConnectionSource(socket)
    println("Socket opened")
    bot.open("Panda Bot", "panda-bot")(connectionSource)
    println("Registration sended")
    bot.join("#drunken-panda")(connectionSource)
    println("Channel joined")
    breakable {
      while (true) {
        val msg = bot.receive()(connectionSource)
        if (msg == null) break else  println(msg)
        if (msg contains "Get the f*ck out!") {
          bot.send("#drunken-panda", "Okey, Okey!")(connectionSource)
          bot.leave("#drunken-panda")(connectionSource)
          break
        } else {
          bot.send("#drunken-panda", "Echoing..." + msg)(connectionSource)
        }
      }
    }
    socket.close()
  }
}

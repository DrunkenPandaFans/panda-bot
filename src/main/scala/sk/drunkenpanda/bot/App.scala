package sk.drunkenpanda.bot

import java.net.Socket
import scala.util.control.Breaks._

object App {

  val bot = new Bot("irc.freenode.net", 6667)

  def main(args: Array[String]): Unit = {
    bot.connect("drunken-panda", "Drunken Panda Bot", "#drunken-panda")
  }
}

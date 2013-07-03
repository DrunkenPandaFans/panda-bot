package sk.drunkenpanda.bot

import java.net.Socket

import sk.drunkenpanda.bot.action._

abstract class ConnectionProvider {
  def apply[A](f: Action[A]): A
}

object ConnectionProvider {
  lazy val freenode = makeConnection("irc.freenode.net", 6667)

  def makeConnection(host: String, port: Int) = 
    new ConnectionProvider {
      def apply[A](f: Action[A]): A = {
        val socket = new Socket(host, port)
        f(socket)
      }
    }
}

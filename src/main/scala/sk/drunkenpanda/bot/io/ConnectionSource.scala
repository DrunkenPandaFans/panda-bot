package sk.drunkenpanda.bot.io

import java.io._
import java.net.Socket

trait ConnectionSource {

  def write(value: String): Unit

  def read[A](f: => BufferedReader => A): A

  def shutdown: Unit
}

class SocketConnectionSource(server: String, port: Int) extends ConnectionSource {
  val socket: Socket = new Socket(server, port)

  def write(value: String) = {
    val osw = new OutputStreamWriter(socket.getOutputStream)
    val pw = new PrintWriter(osw)
    pw.write(value)
    pw.write("\n")
    pw.flush()
  }

  def read[A](f: => BufferedReader => A) = {
    val isr = new InputStreamReader(socket.getInputStream)
    val br = new BufferedReader(isr)
    f(br)
  }

  def shutdown: Unit = socket.close()

}

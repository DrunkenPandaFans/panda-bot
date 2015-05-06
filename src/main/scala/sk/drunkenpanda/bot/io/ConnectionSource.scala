package sk.drunkenpanda.bot.io

import java.io._
import java.net.Socket

import scala.util.Try

trait ConnectionSource {

  def write(value: String): Try[Unit]

  def read: Try[String]

  def shutdown: Try[Unit]
}

class SocketConnectionSource(server: String, port: Int) extends ConnectionSource {
  val socket: Socket = new Socket(server, port)

  def write(value: String) = for {
    osw <- Try(new OutputStreamWriter(socket.getOutputStream))
    pw <- Try(new PrintWriter(osw))
  } yield {
    pw.write(value)
    pw.write("\n")
    pw.flush()
    pw.close
    osw.close
  }

  def read = for {
    isr <- Try(new InputStreamReader(socket.getInputStream))
    br <- Try(new BufferedReader(isr))
  } yield {
    val line = br.readLine
    br.close
    isr.close
    line
  }

  def shutdown = Try(socket.close())

}

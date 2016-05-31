package sk.drunkenpanda.bot.io

import java.io._
import java.net.Socket

import scala.util.Try

trait ConnectionSource {

  def write(value: String): Try[Unit]

  def read: Try[String]

  def shutdown: Try[Unit]
}

class SocketConnectionSource(socket: => Socket) extends ConnectionSource {

  def write(value: String): Try[Unit] = for {
    osw <- Try(new OutputStreamWriter(socket.getOutputStream))
    pw <- Try(new PrintWriter(osw))
  } yield {
    pw.write(value + "\n")
    pw.flush()
  }

  def read: Try[String] = for {
    isr <- Try(new InputStreamReader(socket.getInputStream))
    br <- Try(new BufferedReader(isr))
  } yield br.readLine

  def shutdown: Try[Unit] = Try(socket.close())

}

object SocketConnectionSource {
  def apply(server: String, port: Int): ConnectionSource = new SocketConnectionSource(new Socket(server, port))

  def apply(socket: => Socket): ConnectionSource = new SocketConnectionSource(socket)
}

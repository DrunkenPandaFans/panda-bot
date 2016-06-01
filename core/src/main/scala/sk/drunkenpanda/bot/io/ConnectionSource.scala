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

  private lazy val printWriter =
    for {
      osw <- Try(new OutputStreamWriter(socket.getOutputStream))
    } yield new PrintWriter(osw)

  private lazy val bufferedReader =
    for {
      isr <- Try(new InputStreamReader(socket.getInputStream))
    } yield new BufferedReader(isr)

  def write(value: String): Try[Unit] = for {
    pw <- printWriter
  } yield {
    pw.write(value + "\n")
    pw.flush()
  }

  def read: Try[String] = for {
    br <- bufferedReader
  } yield br.readLine

  def shutdown: Try[Unit] = Try(socket.close())

}

object SocketConnectionSource {
  def apply(server: String, port: Int): SocketConnectionSource =
    new SocketConnectionSource(new Socket(server, port))

  def apply(socket: => Socket): SocketConnectionSource =
    new SocketConnectionSource(socket)
}

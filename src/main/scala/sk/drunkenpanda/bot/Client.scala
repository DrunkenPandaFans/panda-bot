package sk.drunkenpanda.bot

import java.net.Socket
import java.io._

trait IrcClient[S <: ConnectionSource] {
  def join(channel: String): S => Unit

  def open(realname: String, username: String): S => Unit

  def send(to: String, msg: String): S => Unit

  def receive(): S => String

  def leave(channel: String): S => Unit
  
}

class NetworkIrcClient extends IrcClient[SocketConnectionSource] {

  def join(channel: String) = 
    s => s.write(w => w.write("JOIN " + channel))

  def open(realname: String, username: String) =
    s => s.write(w => {
      w.write("NICK " + username + "\n")
      w.write("USER " + username + " 0 * :" + realname)})

  def send(to: String, msg: String) = 
    s => s.write(w => w.write("PRIVMSG " + to + " :" + msg))

  def receive() = s => s.read(r => r.readLine())

  def leave(channel: String) =  s => s.write(w => w.write("PART " + channel))
}

trait ConnectionSource {

  def write[A](f: Writer => A): A

  def read[A](f: BufferedReader => A): A
}

class SocketConnectionSource(socket: Socket) extends ConnectionSource {

  def write[A](f: Writer => A): A = {
    val osw = new OutputStreamWriter(socket.getOutputStream)
    val pw = new PrintWriter(osw)
    try {
      val result = f(pw)
      pw.write("\n")
      pw.flush()
      result
    } finally {
      // if (pw != null) pw.close()
      //if (osw != null) osw.close()
    }
  }

  def read[A](f: BufferedReader => A): A = {
    val isr = new InputStreamReader(socket.getInputStream)
    val br = new BufferedReader(isr)
    try {
      f(br)
    } finally {
      //if (br != null) br.close()
      //if (isr != null) isr.close()
    }
  }

}

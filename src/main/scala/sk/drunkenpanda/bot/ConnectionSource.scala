package sk.drunkenpanda.bot

import java.io._
import java.net.Socket

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

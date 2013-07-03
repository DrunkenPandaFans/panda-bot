package sk.drunkenpanda.bot

import java.io.IOException
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Writer
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.Socket

import sk.drunkenpanda.bot.action._

trait IrcClient {

  def join(channel: String): Action[Boolean]

  def open(nickname: String, realname: String): Action[Boolean]

  def leave(channel: String): Action[Boolean]

  def send(to: String, msg: String): Action[Boolean]

  def retrieve(): Action[String]
}

class SocketIrcClient extends Reader with IrcClient {

  private def write[A](s: Socket)(f: Writer => A): Option[A] = {
    val osw = new OutputStreamWriter(s.getOutputStream())
    val printWriter = new PrintWriter(osw)

    try {
      Some(f(printWriter))
    } catch {
      case e: IOException => None
    } finally {
      if (printWriter != null) printWriter.close()
      if (osw != null) osw.close()
    }
  }

  private def read[A](s: Socket)(f: BufferedReader => A): Option[A] = {
    val isr = new InputStreamReader(s.getInputStream())
    val br = new BufferedReader(isr)

    try {
      Some(f(br))
    } catch {
      case e: IOException => None        
    } finally {
      if (br != null) br.close()
      if (isr != null) isr.close()
    }
  }

  def join(channel: String): Action[Boolean] = new Action({ s =>
    val res = write(s) { w => 
      w.write("JOIN " + channel) 
      true 
    }
    res getOrElse false
  })

  def open(nickname: String, realname: String): Action[Boolean] = new Action({ s =>
    val res = write(s) { w =>
      w.write("NICK " + nickname)
      w.write("USERNAME " + nickname + " 0 * :" + realname)
      true
    } 
    res getOrElse false
  })

  def leave(channel: String): Action[Boolean] = new Action({ s =>
    val res = write(s) { w =>
      w.write("PART " + channel)
      true
    }
    res getOrElse false
  })

  def send(to: String, msg: String): Action[Boolean] = new Action({ s => 
    val res = write(s) { w =>
      w.write("PRIVMSG " + to + " " + msg)
      true
    }

    res getOrElse false
  })

  def retrieve(): Action[String] = new Action({ s =>
    val res = read(s) { r =>
      r.readLine()
    }

    res getOrElse ""
  })
  
}

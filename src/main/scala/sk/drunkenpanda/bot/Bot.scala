package sk.drunkenpanda.bot

import java.net.Socket

class Bot(hostname: String, port: Int) {

  lazy val socketProvider = 
    new SocketConnectionSource(new Socket(hostname, port))

  //def connect(channels: LinearSeq[String]) = 


}

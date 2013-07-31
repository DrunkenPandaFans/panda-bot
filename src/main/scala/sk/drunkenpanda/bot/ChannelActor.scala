package sk.drunkenpanda.bot

import akka.actor._

/**class ChannelActor(source: ConnectionSource[Socket]) extends Actor {
  
  val client = new NetworkIrcClient

  def receive = {
    case Connect(channels, nick, real) => {
      client.open(real, nick)(source)
      channels map { c => client.join(c)(source) }
      while (True) {
        sender ! Message(readMessage)
      }
    }

    case Quit(channel) => {
      client.leave(channel)(source)
    }
    
  }

  def readMessage: String = {
    val msg = client.receive()(source)
    while (msg == null) {
      msg = client.receive(source)
    }
    msg
  }
}*/

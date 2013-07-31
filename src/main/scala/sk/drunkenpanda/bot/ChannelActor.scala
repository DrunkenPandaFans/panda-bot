package sk.drunkenpanda.bot

import akka.actor._

case class Connect(channels: Seq[String], nick: String, real: String)
case class Message(msg: String)
case class Quit(channel: String)

class ChannelActor(source: ConnectionSource[Socket]) extends Actor {
  
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
}

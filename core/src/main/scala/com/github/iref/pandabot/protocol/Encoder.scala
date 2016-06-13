package com.github.iref.pandabot.protocol

object Encoder extends (Message => String) {

  private def encodeServerMessage(msg: ServerMessage) = {
    var out = msg.nick
    msg.name.foreach { name => out = s"$out!$name" }
    msg.host.foreach { host => out = s"$out@$host" }
    out + encode(msg.message)
  }

  /**
   * Encodes IRC messages to string.
   *
   * @param msg the irc protocol message
   * @return irc message encoded in string
   */
  def encode(msg: Message): String = {
    msg match {
      case EmptyMessage(typ, params) => "%s %s".format(typ, params.mkString(" "))
      case TailMessage(typ, params, tail) => "%s %s :%s".format(typ, params.mkString(" "), tail)
      case msg: ServerMessage => encodeServerMessage(msg)
    }
  }

  override def apply(msg: Message): String = encode(msg)
}

package com.github.iref.pandabot.protocol

case class UnknownMessage(typ: Message.Typ, params: Seq[String], tail: Option[String]) extends RuntimeException {
  override def toString: String = {
    val ps = params.mkString(" ")
    val t = tail.map(t => ":" + t).getOrElse("")
    s"Invalid message: $typ $ps :$t"
  }

  override def getMessage: String = toString
}

object RequireProtocolMessage {
  def apply(test: => Boolean, msg: Message): Unit = {
    if (test) {
      throw new UnknownMessage(msg.typ, msg.parameters, msg.tail)
    }
  }
}

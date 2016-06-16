package com.github.iref.pandabot.protocol

import Message._

/**
 * Represents message in IRC protocol
 */
trait Message {

  /**
   * Values should correspond to the constants defined in
   * [[com.github.iref.pandabot.protocol.Message.Types]].
   */
  def typ: Message.Typ

  /**
   * Sequence containing message parameters.
   * Parameter values don't contain whitespace characters.
   */
  def parameters: Seq[String]

  /**
   * Rest of message.
   * Value contains alphanumeric characters and whitespaces other than \r\n.
   */
  def tail: Option[String]

  protected def validate(): Boolean = {
    val whitespaceParams = parameters
      .exists(param => "\\s+".r.findFirstIn(param).isDefined)

    !whitespaceParams && !tail.exists(t => t.contains("\r\n"))
  }

}

object Message {

  /**
   * Type alias for message types.
   */
  type Typ = String

  private[protocol] object Types {
    val PASS: Typ = "PASS"
    val NICK: Typ = "NICK"
    val USER: Typ = "USER"
    val QUIT: Typ = "QUIT"
    val OPER: Typ = "OPER"
    val JOIN: Typ = "JOIN"
    val PART: Typ = "PART"
    val TOPIC: Typ = "TOPIC"
    val NAMES: Typ = "NAMES"
    val LIST: Typ = "LIST"
    val INVITE: Typ = "INVITE"
    val KICK: Typ = "KICK"
    val PRIVMSG: Typ = "PRIVMSG"
    val NOTICE: Typ = "NOTICE"
    val WHO: Typ = "WHO"
    val WHOIS: Typ = "WHOIS"
    val PING: Typ = "PING"
    val PONG: Typ = "PONG"
    val MODE: Typ = "MODE"
  }

}

/**
 * A message without tail.
 */
abstract class EmptyMessage extends Message {
  RequireProtocolMessage(validate, this)

  override val tail: Option[String] = None
}

/**
 * Helpers for working with empty messages.
 */
object EmptyMessage {
  def unapply(emptyMessage: EmptyMessage): Option[(Typ, Seq[String])] = {
    Some((emptyMessage.typ, emptyMessage.parameters))
  }
}

/**
 * Helper for working with empty messages
 */
object TailMessage {
  def unapply(msg: Message): Option[(Typ, Seq[String], String)] = {
    msg.tail.map(tail => (msg.typ, msg.parameters, tail))
  }
}

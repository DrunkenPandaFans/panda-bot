package com.github.iref.pandabot.protocol.commands

import com.github.iref.pandabot.protocol.{ EmptyMessage, Message }
import com.github.iref.pandabot.protocol.Message.Types

/**
 * A password message. Message is used to set connection password.
 */
final case class Pass(password: String) extends EmptyMessage {
  override val typ = Types.PASS
  override def parameters = List(password)
}

/**
 * A nickname message. It's used to give user a nickname or to change
 * existing one.
 */
final case class Nick(nick: String) extends EmptyMessage {
  override def typ = Types.NICK
  override def parameters = List(nick)
}

/**
 * A user message. It's used at beginning of connection to specify client's
 * username, hostname, server name and real name.
 */
final case class User(username: String, host: String, serverName: String, realName: String)
    extends Message {
  override val typ = Types.USER
  override def parameters = List(username, host, serverName)
  override def tail = Option(realName)
}

/**
 * A message sent by server.
 */
final case class ServerMessage(nick: String, name: Option[String], host: Option[String], message: Message)
    extends Message {
  override val typ = message.typ
  override val parameters = message.parameters
  override val tail = message.tail
}

/**
 * A quit message. It's used to end connection session by client.
 * If message is given, this will be sent instead of default message.
 */
final case class Quit(message: Option[String] = None)
    extends EmptyMessage {
  override val typ = Types.QUIT
  override def parameters = message.toList
}

/**
 * A operator message. It's used to obtain operator privileges by a normal user.
 */
final case class Oper(user: String, password: String) extends EmptyMessage {
  override val typ = Types.OPER
  override def parameters = List(user, password)
}
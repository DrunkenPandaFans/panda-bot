package com.github.iref.pandabot.protocol.commands

import cats.data.NonEmptyList
import cats.std.list._
import com.github.iref.pandabot.protocol.Message
import com.github.iref.pandabot.protocol.Message.Types

/**
 * A private message. It's used to send private message between users.
 *
 * Note, receiver can be user, host mask or server mask.
 * Wildcards '*' and '?' are allowed, if they aren't used after last '.' in the mask.
 */
final case class PrivMsg(receivers: NonEmptyList[String], text: String) extends Message {
  override val typ = Types.PRIVMSG
  override def parameters = receivers.unwrap
  override def tail = Option(text)
}

/**
 * A notice message. It's used to send message to user with given nickname.
 */
final case class Notice(nick: String, text: String) extends Message {
  override val typ = Types.NOTICE
  override def parameters = List(nick)
  override def tail = Option(text)
}
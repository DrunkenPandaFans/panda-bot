package com.github.iref.pandabot.protocol.commands

import cats.data.NonEmptyList
import cats.std.list._
import com.github.iref.pandabot.protocol.EmptyMessage
import com.github.iref.pandabot.protocol.Message.Types

/**
 * A ping message. Ping message is sent by server to check presence of client.
 */
final case class Ping(servers: NonEmptyList[String]) extends EmptyMessage {
  override val typ = Types.PING
  override def parameters = servers.unwrap
}

/**
 * A reply to ping messages.
 */
final case class Pong(daemons: NonEmptyList[String]) extends EmptyMessage {
  override val typ = Types.PONG
  override def parameters = daemons.unwrap
}

package com.github.iref.pandabot.protocol.commands

import cats.data.NonEmptyList
import cats.std.list._
import com.github.iref.pandabot.protocol.{ EmptyMessage, Message }
import com.github.iref.pandabot.protocol.Message.Types

/**
 * A join message. It's used by client to join specific channels.
 *
 * Note, i-th password is password to i-th channel in channels list.
 */
final case class Join(channels: NonEmptyList[String], passwords: Seq[String])
    extends EmptyMessage {
  override val typ = Types.JOIN
  override def parameters =
    List(channels.unwrap.mkString(","), passwords.mkString(","))
}

/**
 * A part message. It removes user from the list of active users of all channels.
 */
final case class Part(channels: Seq[String]) extends EmptyMessage {
  override val typ = Types.PART
  override def parameters = List(channels.mkString(","))
}

/**
 * A topic message. It's used to get or set new channel topic.
 *
 * Note, channel topic is returned if topic isn't part of message.
 * If topic is provided, the topic of the channel is changed, if the channel
 * mode permits change.
 */
final case class Topic(channel: String, topic: Option[String]) extends EmptyMessage {
  override val typ = Types.TOPIC
  override def parameters = List(channel) ++ topic.toList
}

/**
 * A invite message. It's used to invite users to channel.
 */
final case class Invite(nick: String, channel: String) extends EmptyMessage {
  override val typ = Types.INVITE
  override def parameters = List(nick, channel)
}

/**
 * A kick message. It's used to forcibly remove user from channel.
 *
 * Note, only channel operator may kick users from channel.
 */
final case class Kick(channel: String, user: String, comment: Option[String])
    extends Message {
  override val typ = Types.KICK
  override def parameters = List(channel, user)
  override def tail: Option[String] = comment
}

/**
 * A names message. It's used to list nicknames of all users in given channels
 * that are visible to client.
 */
final case class Names(channels: NonEmptyList[String]) extends EmptyMessage {
  override val typ = Types.NAMES
  override def parameters = channels.unwrap
}

/**
 * A list message. It's used to list channels and their topics.
 *
 * Note, If channels list is used only topics of those channels are retrieved.
 */
final case class ChannelList(channels: NonEmptyList[String]) extends EmptyMessage {
  override val typ = Types.LIST
  override def parameters = channels.unwrap
}

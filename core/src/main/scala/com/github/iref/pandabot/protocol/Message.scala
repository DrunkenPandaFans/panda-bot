package com.github.iref.pandabot.protocol

import Message._
import cats.data.NonEmptyList
import cats.std.list._

/**
 * Represents message in IRC protocol
 */
sealed trait Message {

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

/**
 * A message sent by server.
 */
final case class ServerMessage(nick: String, name: Option[String], host: Option[String], message: Message)
    extends Message {
  RequireProtocolMessage(validate, this)

  override val typ = message.typ
  override val parameters = message.parameters
  override val tail = message.tail
}

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

/**
 * A who message. It's used to generate query whick returns information about
 * client that matches name parameter.
 *
 * Note, in absence of name parameter all visible users are listed.
 */
final case class Who(name: Option[String], operatorsOnly: Boolean = false) extends Message {
  RequireProtocolMessage(validate(), this)

  override val typ = Types.WHO
  override val parameters =
    name.toList ++ (if (operatorsOnly) List("o") else Nil)
  override val tail = None
}

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

/**
 * A mode message. It allows both channels and users to have their mode changed.
 */
sealed abstract class Mode(modeId: String) extends EmptyMessage {
  RequireProtocolMessage(validate, this)

  override val typ = Types.MODE
  override def parameters = {
    val mode = "%s%s".format(unset.map(u => if (u) "+" else "-").getOrElse(""), modeId)
    List(user, mode)
  }

  /**
   * A flag if mode is set.
   */
  def unset: Option[Boolean]

  /**
   * A user or channel whose mode is being changed.
   */
  def user: String
}

/**
 * Channel mode message. It allows channel to have its mode changed.
 */
sealed abstract class ChannelMode(modeId: String, suffix: String = "")
    extends Mode(modeId) {
  override val user = channel
  override val parameters = super.parameters :+ suffix

  RequireProtocolMessage(validate, this)

  /**
   * A channel whose mode is being changed.
   */
  def channel: String
}

/**
 * Invisibility mode. If turned on user is invisible to other users.
 */
final case class UserInvisibleMode(unset: Option[Boolean], user: String) extends Mode("i")

/**
 * Server notices mode. If turned on user retrieves server notices.
 */
final case class UserRetrieveNoticesMode(unset: Option[Boolean], user: String) extends Mode("s")

/**
 * Wallops mode. If turned on user retrieves wallops.
 */
final case class UserRetrieveWallopsMode(unset: Option[Boolean], user: String) extends Mode("w")

/**
 * Operator mode. Gives/takes operator privileges to/from user.
 */
final case class UserOperatorMode(unset: Option[Boolean], user: String) extends Mode("o")

/**
 * Channel operator mode. Gives/takes channel operator privileges to user.
 */
final case class ChannelOperatorMode(unset: Option[Boolean], channel: String, nick: String)
  extends ChannelMode("o", nick)

/**
 * Private mode. Sets channel as private.
 */
final case class ChannelPrivateMode(unset: Option[Boolean], channel: String) extends ChannelMode("p")

/**
 * Secret mode. Allows channel to become secret channel, not visible to users.
 */
final case class ChannelSecretMode(unset: Option[Boolean], channel: String) extends ChannelMode("s")

/**
 * Invite-only mode. New user can join channel only if they're invited by other user.
 */
final case class ChannelInviteOnlyMode(unset: Option[Boolean], channel: String) extends ChannelMode("i")

/**
 * Topic operator only mode. Topic can be set only by channel operators.
 */
final case class ChannelTopicOpOnlyMode(unset: Option[Boolean], channel: String) extends ChannelMode("t")

/**
 * No outside messages mode. Clients can't send messages to channel if they
 * didn't join channel.
 */
final case class ChannelNoOutsideMessageMode(unset: Option[Boolean], channel: String) extends ChannelMode("n")

/**
 * Moderator mode. Channel is moderated.
 */
final case class ChannelModeratedMode(unset: Option[Boolean], channel: String) extends ChannelMode("m")

/**
 * User-limit mode. Limits number of users in channel.
 */
final case class ChannelUserLimitMode(unset: Option[Boolean], channel: String, limit: Int)
  extends ChannelMode("l", limit.toString)

/**
 * Ban mask mode. Ban users from channel if their username or hostname matches mask.
 */
final case class ChannelBanMaskMode(unset: Option[Boolean], channel: String, mask: String)
  extends ChannelMode("b", mask)

/**
 * Voice mode. It gives/takes ability to speak in moderated channels.
 */
final case class ChannelVoiceMode(unset: Option[Boolean], channel: String, nick: String)
  extends ChannelMode("v", nick)

/**
 * Key mode. It sets channel password.
 */
final case class ChannelKeyMode(unset: Option[Boolean], channel: String, key: String)
  extends ChannelMode("k", key)

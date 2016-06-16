package com.github.iref.pandabot.protocol.commands

import com.github.iref.pandabot.protocol.{ Message, RequireProtocolMessage }
import com.github.iref.pandabot.protocol.Message.Types

/**
 * A mode message. It allows both channels and users to have their mode changed.
 */
sealed trait Mode extends Message {
  override val typ = Types.MODE

  override def parameters = {
    val mode = "%s%s".format(unset.map(u => if (u) "+" else "-").getOrElse(""), modeId)
    List(modeId, mode)
  }

  override val tail: Option[String] = None

  /**
   * A user or channel that applied mode to.
   */
  def subject: String

  /**
   * A mode identifier.
   */
  def modeId: String

  /**
   * A flag if mode is set.
   */
  def unset: Option[Boolean]

  /**
   * Optional argument for mode modification.
   */
  def suffix: String
}

/**
 * A user mode message. Implementations change either how user is seen by
 * other users or what extra messages are sent to user.
 */
sealed abstract class UserMode(val modeId: String) extends Mode {
  RequireProtocolMessage(validate, this)

  override val subject = user

  override val typ = Types.MODE

  override val suffix = ""

  /**
   * A user or channel whose mode is being changed.
   */
  def user: String
}

/**
 * Channel mode message. It allows channel to have its mode changed.
 */
sealed abstract class ChannelMode(val modeId: String, val suffix: String = "") extends Mode {
  RequireProtocolMessage(validate, this)

  override val subject = channel

  override val parameters = super.parameters :+ suffix

  /**
   * A channel whose mode is being changed.
   */
  def channel: String
}

/**
 * Invisibility mode. If turned on user is invisible to other users.
 */
final case class UserInvisibleMode(unset: Option[Boolean], user: String) extends UserMode("i")

/**
 * Server notices mode. If turned on user retrieves server notices.
 */
final case class UserRetrieveNoticesMode(unset: Option[Boolean], user: String) extends UserMode("s")

/**
 * Wallops mode. If turned on user retrieves wallops.
 */
final case class UserRetrieveWallopsMode(unset: Option[Boolean], user: String) extends UserMode("w")

/**
 * Operator mode. Gives/takes operator privileges to/from user.
 */
final case class UserOperatorMode(unset: Option[Boolean], user: String) extends UserMode("o")

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

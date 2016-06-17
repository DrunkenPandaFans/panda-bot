package com.github.iref.pandabot

import cats.data.NonEmptyList
import cats.syntax.all._
import com.github.iref.pandabot.protocol.Message
import com.github.iref.pandabot.protocol.commands._
import org.scalacheck.Gen

trait Generators {
  import Gen._

  val genNonEmptyAlphaNumStr: Gen[String] =
    for {
      n <- posNum[Int]
      name <- listOfN(n, alphaNumChar).toString
    } yield name

  val genChannel: Gen[String] =
    for {
      name <- genNonEmptyAlphaNumStr
      mask <- oneOf("#", "&")
    } yield mask + name

  def nonEmptyList[A](g: Gen[A]): Gen[NonEmptyList[A]] =
    for {
      n <- posNum[Int]
      channels <- listOfN(n, g)
    } yield channels.toNel.get

  val nick: Gen[Message] = genNonEmptyAlphaNumStr.map(Nick.apply)

  val pass: Gen[Message] = genNonEmptyAlphaNumStr.map(Pass.apply)

  val user: Gen[Message] = for {
    username <- genNonEmptyAlphaNumStr
    hostname <- alphaStr if !hostname.isEmpty
    serverName <- alphaStr if !serverName.isEmpty
    realName <- genNonEmptyAlphaNumStr
  } yield User(username, hostname, serverName, realName)

  val oper: Gen[Message] = for {
    nickname <- genNonEmptyAlphaNumStr
    password <- genNonEmptyAlphaNumStr
  } yield Oper(nickname, password)

  val quit: Gen[Message] = for {
    msg <- option(alphaStr)
  } yield Quit(msg)

  val join: Gen[Message] = for {
    n <- posNum[Int]
    channels <- listOfN(n, genChannel)
    keys <- listOf(genNonEmptyAlphaNumStr) if keys.length <= n
  } yield Join(channels.toNel.get, keys)

  val part: Gen[Message] = for {
    n <- posNum[Int]
    channels <- listOfN(n, genChannel)
  } yield Part(channels)

  val topic: Gen[Message] = for {
    channel <- genChannel
    topic <- option(genNonEmptyAlphaNumStr)
  } yield Topic(channel, topic)

  val names: Gen[Message] = nonEmptyList(genChannel).map(Names.apply)

  val channelLists: Gen[Message] = nonEmptyList(genChannel).map(ChannelList.apply)

  val invite: Gen[Message] = for {
    nick <- genNonEmptyAlphaNumStr
    channel <- genChannel
  } yield Invite(nick, channel)

  val kick: Gen[Message] = for {
    channel <- genChannel
    nick <- genNonEmptyAlphaNumStr
    comment <- option(genNonEmptyAlphaNumStr)
  } yield Kick(channel, nick, comment)

  val privMsg: Gen[Message] = for {
    n <- posNum[Int]
    receivers <- nonEmptyList(genNonEmptyAlphaNumStr)
    text <- genNonEmptyAlphaNumStr
  } yield PrivMsg(receivers, text)

  val notice: Gen[Message] = for {
    nick <- genNonEmptyAlphaNumStr
    text <- genNonEmptyAlphaNumStr
  } yield Notice(nick, text)

  val ping: Gen[Message] =
    nonEmptyList(genNonEmptyAlphaNumStr).map(Ping.apply)

  val pong: Gen[Message] =
    nonEmptyList(genNonEmptyAlphaNumStr).map(Pong.apply)

  val who: Gen[Message] = for {
    name <- option(genNonEmptyAlphaNumStr)
    op <- oneOf(false, true)
  } yield Who(name, op)

  val userMode: Gen[Mode] =
    for {
      m <- oneOf(UserOperatorMode, UserInvisibleMode, UserRetrieveNoticesMode, UserRetrieveWallopsMode)
      u <- genNonEmptyAlphaNumStr
      unset <- option(oneOf(true, false))
    } yield m.apply(unset, u)

  val channelMode: Gen[Mode] =
    for {
      m <- oneOf(ChannelModeratedMode, ChannelNoOutsideMessageMode,
        ChannelTopicOpOnlyMode, ChannelInviteOnlyMode, ChannelPrivateMode, ChannelSecretMode)
      ch <- genChannel
      unset <- option(oneOf(true, false))
    } yield m.apply(unset, ch)

  val channelUserMode: Gen[Mode] =
    for {
      m <- oneOf(ChannelOperatorMode, ChannelVoiceMode)
      ch <- genChannel
      nick <- genNonEmptyAlphaNumStr
      unset <- option(oneOf(true, false))
    } yield m.apply(unset, ch, nick)

  val channelKeyMode: Gen[Mode] =
    for {
      ch <- genChannel
      key <- genNonEmptyAlphaNumStr
      unset <- option(oneOf(true, false))
    } yield ChannelKeyMode(unset, ch, key)

  val userLimitMode: Gen[Mode] =
    for {
      ch <- genChannel
      limit <- posNum[Int]
      unset <- option(oneOf(true, false))
    } yield ChannelUserLimitMode(unset, ch, limit)

  val banMaskMode: Gen[Mode] =
    for {
      ch <- genChannel
      mask <- genNonEmptyAlphaNumStr
      unset <- option(oneOf(true, false))
    } yield ChannelBanMaskMode(unset, ch, mask)

  val mode: Gen[Mode] = oneOf(userMode, channelMode, channelUserMode, channelKeyMode, userLimitMode, banMaskMode)

  val message: Gen[Message] = oneOf(pass, nick, user, quit, oper, join, part, topic, names, channelLists,
    kick, invite, privMsg, notice, ping, pong, who, mode)
}

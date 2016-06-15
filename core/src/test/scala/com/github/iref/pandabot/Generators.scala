package com.github.iref.pandabot

import cats.syntax.all._
import com.github.iref.pandabot.protocol._
import org.scalacheck.Gen

trait Generators {
  import Gen._

  val genNonEmptyAlphaNumStr: Gen[String] =
    for {
      n <- posNum[Int]
      name <- listOf(n, alphaNumChar).toString
    } yield name

  val genChannel: Gen[String] =
    for {
      name <- genNonEmptyAlphaNumStr
      mask <- oneOf("#", "&")
    } yield mask + name

  val genNick: Gen[Message] = genNonEmptyAlphaNumStr.map(Nick.apply)

  val genPassword: Gen[Message] = genNonEmptyAlphaNumStr.map(Pass.apply)

  val genUser: Gen[Message] = for {
    username <- genNonEmptyAlphaNumStr
    hostname <- alphaStr if !hostname.isEmpty
    serverName <- alphaStr if !serverName.isEmpty
    realName <- genNonEmptyAlphaNumStr
  } yield User(username, hostname, serverName, realName)

  val genOper: Gen[Message] = for {
    nickname <- genNonEmptyAlphaNumStr
    password <- genNonEmptyAlphaNumStr
  } yield Oper(nickname, password)

  val genQuit: Gen[Message] = for {
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

  val names: Gen[Message] = for {
    n <- posNum[Int]
    channels <- listOfN(n, genChannel)
  } yield Names(channels.toNel.get)

  val channelLists: Gen[Message] = for {
    n <- posNum[Int]
    channels <- listOfN(n, genChannel)
  } yield ChannelList(channels.toNel.get)

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
    receivers <- listOfN(n, oneOf(genChannel, genNonEmptyAlphaNumStr))
    text <- genNonEmptyAlphaNumStr
  } yield PrivMsg(receivers.toNel.get, text)

  val notice: Gen[Message] = for {
    nick <- genNonEmptyAlphaNumStr
    text <- genNonEmptyAlphaNumStr
  } yield Notice(nick, text)

  val ping: Gen[Message] = for {
    n <- posNum[Int]
    servers <- listOfN(n, genNonEmptyAlphaNumStr)
  } yield Ping(servers.toNel.get)

  val pong: Gen[Message] = for {
    n <- posNum[Int]
    daemons <- listOfN(n, genNonEmptyAlphaNumStr)
  } yield Pong(daemons.toNel.get)

  val who: Gen[Message] = for {
    name <- option(genNonEmptyAlphaNumStr)
    op <- oneOf(false, true)
  } yield Who(name, op)
}

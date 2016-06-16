package com.github.iref.pandabot.protocol

import cats.data.NonEmptyList
import com.github.iref.pandabot.protocol.Message.{ Typ, Types }
import com.github.iref.pandabot.protocol.commands._

class Decoder(decoders: Map[Typ, PartialFunction[List[String], Message]]) extends (String => Option[Message]) {

  private val ServerInfo = "([^!]+)!?([^@]*)?@?(.+)?".r

  private def decodeServerMessage(cmd: String) = {
    cmd.split(" ", 1).toList match {
      case prefix :: command :: Nil => {
        val ServerInfo(nick, name, host) = prefix
        decode(command)
          .map(msg => ServerMessage(nick, Option(name), Option(host), msg))
      }
      case _ => None
    }
  }

  private def decodeMessage(tokens: List[String]): Option[Message] = {
    tokens match {
      case cmd :: args => {
        val decoder = decoders.get(cmd.toUpperCase)
        decoder.flatMap(_.lift(args))
      }
      case _ => None
    }
  }

  private def isServerMessage(msg: String): Boolean = msg.startsWith(":")

  def decode(msg: String): Option[Message] = {
    if (isServerMessage(msg)) {
      decodeServerMessage(msg)
    } else {
      msg.split(":", 1).toList match {
        case params :: tail :: Nil => {
          val tokens = params.split(" ") :+ tail
          decodeMessage(tokens.toList)
        }
        case params :: Nil => decodeMessage(params.split(" ").toList)
      }
    }
  }

  override def apply(msg: String): Option[Message] = decode(msg)
}

object SimpleStringDecoder extends Decoder(Decoder.decoders)

object Decoder {

  private def decodeNonEmptyList(arg: String) = {
    val first :: rest = arg.split(",").toList
    NonEmptyList(first, rest)
  }

  private[protocol] val decoders: Map[Typ, PartialFunction[List[String], Message]] =
    Map(
      (Types.PASS -> {
        case password :: Nil => Pass(password)
      }),
      (Types.NICK -> {
        case nick :: Nil => Nick(nick)
      }),
      (Types.USER -> {
        case username :: hostname :: serverName :: realName :: Nil =>
          User(username, hostname, serverName, realName)
      }),
      (Types.JOIN -> {
        case channels :: Nil => Join(decodeNonEmptyList(channels), Nil)
        case channels :: keys :: Nil => Join(decodeNonEmptyList(channels), keys.split(","))
      }),
      (Types.PART -> {
        case channels :: Nil => Part(channels.split(","))
      }),
      (Types.QUIT -> {
        case Nil => Quit()
        case message :: Nil => Quit(Option(message))
      }),
      (Types.OPER -> {
        case nick :: password :: Nil => Oper(nick, password)
      }),
      (Types.TOPIC -> {
        case channel :: Nil => Topic(channel, None)
        case channel :: topic :: Nil => Topic(channel, Option(topic))
      }),
      (Types.NAMES -> {
        case channels :: Nil => Names(decodeNonEmptyList(channels))
      }),
      (Types.LIST -> {
        case channels :: Nil => ChannelList(decodeNonEmptyList(channels))
      }),
      (Types.INVITE -> {
        case nick :: channel :: Nil => Invite(nick, channel)
      }),
      (Types.KICK -> {
        case channel :: user :: Nil => Kick(channel, user, None)
        case channel :: user :: reason :: Nil => Kick(channel, user, Option(reason))
      }),
      (Types.PRIVMSG -> {
        case receivers :: text :: Nil => PrivMsg(decodeNonEmptyList(receivers), text)
      }),
      (Types.NOTICE -> {
        case nick :: text :: Nil => Notice(nick, text)
      }),
      (Types.PING -> {
        case servers :: Nil => Ping(decodeNonEmptyList(servers))
      }),
      (Types.PONG -> {
        case daemons :: Nil => Pong(decodeNonEmptyList(daemons))
      }),
      (Types.WHO -> {
        case "o" :: Nil => Who(None, true)
        case Nil => Who(None, false)
        case name :: Nil => Who(Option(name), false)
        case name :: "o" :: Nil => Who(Option(name), true)
      })
    )
}

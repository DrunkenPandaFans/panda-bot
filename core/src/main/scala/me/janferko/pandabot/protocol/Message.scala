package me.janferko.pandabot.protocol

sealed abstract class Message {
  def command: Command
  def prefix: Option[Prefix]
}

object Message {
  def outgoing(command: Command): Message = ProtocolMessage(command, None)

  def incoming(command: Command, prefix: Option[Prefix]): Message = ProtocolMessage(command, prefix)

  private[protocol] case class ProtocolMessage(command: Command, prefix: Option[Prefix]) extends Message
}



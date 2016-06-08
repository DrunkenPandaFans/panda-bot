package com.github.iref.pandabot.protocol

/**
 * Represents message in IRC protocol
 */
trait Message {

  /**
   * Values should correspond to the constants defined in
   * [[com.github.iref.pandabot.protocol.Message.Types]].
   */
  def typ: Message.Typ

  /**
   * List of message parameters.
   * Parameter values don't contain whitespace characters.
   */
  def parameters: List[String]

  /**
   * Rest of message.
   * Value contains alphanumeric characters and whitespaces other than \r\n.
   */
  def tail: Option[String]

  protected def validate(): Boolean = {
    val whitespaceParams = parameters
      .filter(param => "\\s+".r.findFirstIn(param).isDefined)

    whitespaceParams.isEmpty && !tail.find(t => t.contains("\r\n")).isEmpty
  }

}

object Message {

  type Typ = String

  private[protocol] object Types {
    val PASS = "PASS"
    val NICK = "NICK"
    val USER = "USER"
    val QUIT = "QUIT"
    val OPER = "OPER"
    val JOIN = "JOIN"
  }

  abstract class EmptyMessage(val typ: Typ, val parameters: List[String]) extends Message {
    override val tail: Option[String] = None
    RequireProtocolMessage(validate(), this)
  }

  abstract class TextMessage(val typ: Typ, val parameters: List[String], text: String) extends Message {
    override val tail: Option[String] = Option(text)
    RequireProtocolMessage(validate(), this)
  }

  // Registration messages
  case class Pass(password: String) extends EmptyMessage(Types.PASS, List(password))
  case class Nick(nick: String) extends EmptyMessage(Types.NICK, List(nick))
  case class User(username: String, host: String, serverName: String, realName: String)
      extends TextMessage(Types.USER, List(username, host, serverName), realName)
  case class Quit(message: Option[String] = None)
      extends EmptyMessage(Types.QUIT, message.toList)
  case class Oper(user: String, password: String) extends EmptyMessage(Types.OPER, List(user, password))

  // Channel messages
  case class Join(channels: List[String], passwords: List[String])
      extends EmptyMessage(Types.JOIN, List(channels.mkString(","), passwords.mkString(",")))
}
package sk.drunkenpanda.bot

sealed trait Message

case object Unknown extends Message

case class PrivateMessage(from: String, text: String) extends Message
case class Response(to: String, text: String) extends Message
case class Ping(hash: String) extends Message
case class Pong(hash: String) extends Message
case class Notice(note: String) extends Message
case class Join(channel: String) extends Message
case class Leave(channel: String) extends Message
case class User(username: String, realName: String) extends Message
case class Nick(nickname: String) extends Message

object Message {

  lazy val privateMessagePattern = ".*PRIVMSG (\\S+) :(.+)".r

  lazy val pingPattern = ".*PING :(\\S+)".r

  lazy val noticePattern = ".*NOTICE :(.+)".r

  def parse(message: String): Message = message match {
    case privateMessagePattern(from, text) => new PrivateMessage(from, text)
    case pingPattern(hash) => Ping(hash)
    case noticePattern(note) => new Notice(note)
    case _ => Unknown
  }

  def print(message: Message): String = message match {
    case Response(to, text) => s"PRIVMSG $to :$text"
    case Pong(hash) => s"PONG :$hash"
    case Notice(note) => s"NOTICE :$note"
    case Join(channel) => s"JOIN $channel"
    case Leave(channel) => s"PART $channel"
    case User(username, realName)  => s"USER $username 0 * :$realName"
    case Nick(nickname) => s"NICK $nickname"
    case _ => ""
  }
}

package sk.drunkenpanda.bot

sealed trait Message
case object Unknown extends Message
case class PrivateMessage(to: String, text: String) extends Message
case class Ping(hash: String) extends Message
case class Pong(hash: String) extends Message
case class Notice(note: String) extends Message

object Message {

  lazy val privateMessagePattern = ".*PRIVMSG (\\S+) :(.+)".r

  lazy val pingPattern = ".*PING :(\\S+)".r

  lazy val noticePattern = ".*NOTICE (.+)".r

  def parse(message: String) = message match {
    case privateMessagePattern(from, text) => new PrivateMessage(from, text)
    case pingPattern(hash) => Ping(hash)
    case noticePattern(note) => new Notice(note)
    case _ => Unknown
  }
 
  def print(message: Message) = message match {
    case PrivateMessage(to, text) => "PRIVMSG " + to + " :" + text
    case Pong(hash) => "PONG :" + hash
  	case Notice(note) => "NOTICE " + note
  	case _ => ""
  }
}

package sk.drunkenpanda.bot

sealed trait Message
case object Unknown extends Message
case class PrivateMessage(to: String, text: String) extends Message
case class Ping(hash: String) extends Message
case class Notice(note: String) extends Message

object Message {

  lazy val privateMessagePattern = "PRIVMSG (\\S+) :(.+)".r

  lazy val pingPattern = "PING :(\\S+)".r

  lazy val noticePattern = "NOTICE (.+)".r

  override def parse(message: String) = message match {
    case privateMessagePattern(from, text) => new PrivateMessage(from, text)
    case pingPattern(hash) => Ping(hash)
    case noticePattern(note) => new Notice(note)
    case _ => Unknown
  }
}

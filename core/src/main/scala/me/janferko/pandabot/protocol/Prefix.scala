package me.janferko.pandabot.protocol

sealed abstract class Prefix extends Product with Serializable {
  def nickname: Option[Nickname]

  def username: Option[Username]

  def hostname: Option[Hostname]
}

object Prefix {

  def server(hostname: Hostname): Prefix = ServerPrefix(hostname)

  def user(nickname: Nickname, username: Option[Username], hostname: Option[Hostname]): Prefix =
    UserPrefix(nickname, username, hostname)

  private[protocol] case class ServerPrefix(host: Hostname) extends Prefix {
    override def nickname: Option[Nickname] = None
    override def username: Option[Username] = None
    override def hostname: Option[Hostname] = Option(host)
  }

  private[protocol] case class UserPrefix(
      nick: Nickname,
      username: Option[Username],
      hostname: Option[Hostname]) extends Prefix {
    override def nickname: Option[Nickname] = Option(nick)
  }
}

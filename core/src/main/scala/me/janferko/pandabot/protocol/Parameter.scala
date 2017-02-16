package me.janferko.pandabot.protocol

import cats.syntax.either._

sealed abstract class Parameter extends Product with Serializable {
  def get: String
}

object Parameter {
  private[protocol] val SpecialCharacters = Seq('-', '[', ']', '\\', '`', '^', '{', '}')
}

final case class Nickname(get: String) extends Parameter
object Nickname {
  def validate(value: String): Either[ProtocolError, Nickname] = value match {
    case _ if value.trim.isEmpty          => Either.left(EmptyParameterValue("Nickname"))
    case _ if !onlyValidCharacters(value) => Either.left(UnexpectedCharacter("Nickname", value))
    case _                                => Either.right(Nickname(value))
  }

  private def onlyValidCharacters(value: String) = {
    value.forall(c => c.isLetterOrDigit || Parameter.SpecialCharacters.contains(c))
  }
}

final case class Username(get: String) extends Parameter
object Username {
  def validate(value: String): Either[ProtocolError, Username] = value match {
    case _ if value.trim.isEmpty           => Either.left(EmptyParameterValue("Username"))
    case _ if value.exists(_.isWhitespace) => Either.left(UnexpectedWhitespaceCharacters("Username", value))
    case _                                 => Either.right(Username(value))
  }
}

final case class Hostname(get: String) extends Parameter
object Hostname {
  /**
   * Regular expression for checking hostname labels.
   *
   * Label must:
   * 1. Labels cannot start or end with hyphen
   * 2. Labels are at most 63 characters long
   * 3. Labels can contain only letters, digits or hyphen
   */
  private val LabelRegExp = "(?i)[a-z0-9][a-z0-9\\-]{0,62}[!-]"

  def validate(value: String): Either[ProtocolError, Hostname] = value match {
    case _ if value.trim.isEmpty             => Either.left(EmptyParameterValue("Hostname"))
    case _ if value.length > 255             => Either.left(HostnameTooLong(value))
    case _ if value.last == '.'              => Either.left(HostnameEndsWithDot(value))
    case _ if !checkLabels(value.split(".")) => Either.left(WrongHostnameLabel(value))
    case _                                   => Either.right(Hostname(value))
  }

  private def checkLabels(labels: Array[String]): Boolean =
    labels.forall(_.matches(LabelRegExp))
}

final case class Password(get: String) extends Parameter
final case class Channel(get: String) extends Parameter


final case class Text(get: String)

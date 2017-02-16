package me.janferko.pandabot.protocol

sealed abstract class ProtocolError extends Product with Serializable {
  def message: String
}

final case class EmptyParameterValue(parameterName: String) extends ProtocolError {
  override def message: String = s"Parameter [$parameterName] value was empty."
}

final case class UnexpectedCharacter(parameterName: String, parameterValue: String) extends ProtocolError {
  override def message: String =
    s"""
    |$parameterName [$parameterValue] contains unexpected characters.
    |Parameter can contain only letters, digits or [${Parameter.SpecialCharacters.mkString(", ")}].
    |""".stripMargin
}

final case class UnexpectedWhitespaceCharacters(parameterName: String, parameterValue: String) extends ProtocolError {
  override def message: String = s"$parameterName [$parameterValue] must not contain whitespace character"
}

final case class HostnameEndsWithDot(parameterValue: String) extends ProtocolError {
  override def message: String = s"Hostname [$parameterValue] must not end with dot (.)"
}

final case class WrongHostnameLabel(parameterValue: String) extends ProtocolError {
  override def message: String =
    s"""
       |Hostname [$parameterValue] labels may contain only letters, digits and hyphen.
       |Labels cannot start or end with hyphen and have between 1 and 63 characters.
     """.stripMargin
}

final case class HostnameTooLong(parameterValue: String) extends ProtocolError {
  override def message: String = s"Hostname [$parameterValue] must be at most 255 characters long."
}

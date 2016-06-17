package com.github.iref.pandabot.protocol

import cats.syntax.xor._

/**
 * A IRC command parameter.
 *
 * Parameter value can't contain whitespace characters and it can't start with
 * colon (':' character).
 */
sealed abstract class Param {
  def value: String
}

private[protocol] object Param {

  def validate(value: String): ParameterResult[String] = {
    if (value.exists(_.isWhitespace)) {
      InvalidParameterFailure(s"Parameter [$value] contains whitespaces.").left
    } else if (value.startsWith(":")) {
      InvalidParameterFailure(s"Parameter [$value] starts with ':'.").left
    } else {
      value.right
    }
  }
}

/**
 * A nickname parameter.
 *
 * Values must start with letter and can contain only alphanumeric characters, [, ], \, `, ^, {, }.
 */
final case class Nickname private (value: String) extends Param

object Nickname {

  /**
   * Tries to create new nickname from given value.
   *
   * If value isn't valid nickname, it captures validation failure.
   */
  def from(value: String): ParameterResult[Nickname] =
    if (value.matches("[a-zA-Z][\\S\\[\\]\\\\`\\^\\{\\}]+")) {
      Nickname(value).right
    } else {
      InvalidParameterFailure(
        "Nickname must contain only alphanumeric characters, '[', ']', '\\', '`', '^', '{', '}'"
      ).left
    }
}

/**
 * A username parameter.
 *
 * Value can't contain whitespace characters and it can't start with
 * colon (':' character).
 */
final case class Username private (value: String) extends Param

object Username {

  /**
   * Tries to create new username from given value.
   *
   * If value isn't valid username, it captures validation failure.
   */
  def from(value: String): ParameterResult[Username] =
    Param.validate(value).map(Username.apply)
}

/**
 * A IRC channel parameter.
 *
 * Valid name must start with '#' or '&' and must not contain any whitespace
 * character.
 */
final case class Channel private (value: String) extends Param

object Channel {

  /**
   * Tries to create new channel parameter from given name.
   *
   * If name is invalid, it captures reason of validation failure.
   */
  def from(name: String): ParameterResult[Channel] =
    Param.validate(name).flatMap { name =>
      if (name.startsWith("#") || name.startsWith("&")) {
        Channel(name).right
      } else {
        InvalidParameterFailure(s"Channel name [$name] doesn't start with '#' or '&'.").left
      }
    }
}

/**
 * A IRC hostname parameter.
 *
 * Hostname can only labels separated by dots '.'.
 * Labels consist of case-insensitive ASCII letters 'a' - 'z', digits '0' - '9'
 * and hyphen ('-').
 *
 * @see https://en.wikipedia.org/wiki/Hostname
 */
final case class Hostname private (value: String) extends Param

object Hostname {

  /**
   * Tries to create new hostname parameter from given name.
   *
   * If value is invalid, it captures reason of validation failure.
   */
  def from(value: String): ParameterResult[Hostname] =
    if (value.matches("([a-zA-Z0-9\\-]+\\.)*[a-zA-Z0-9\\-]+")) {
      Hostname(value).right
    } else {
      InvalidParameterFailure("Hostname can contain only letters, digits, hyphen (-) or dot (.)").left
    }
}
package com.github.iref.pandabot.protocol.commands

import com.github.iref.pandabot.protocol.Message
import com.github.iref.pandabot.protocol.Message.Types

/**
 * A who message. It's used to generate query whick returns information about
 * client that matches name parameter.
 *
 * Note, in absence of name parameter all visible users are listed.
 */
final case class Who(name: Option[String], operatorsOnly: Boolean = false) extends Message {
  override val typ = Types.WHO
  override val parameters =
    name.toList ++ (if (operatorsOnly) List("o") else Nil)
  override val tail = None
}

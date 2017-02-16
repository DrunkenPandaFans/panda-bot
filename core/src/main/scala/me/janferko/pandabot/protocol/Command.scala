package me.janferko.pandabot.protocol

sealed abstract class Command extends Product with Serializable {
  def name: String

  def parameters: Seq[Parameter]

  def text: Option[Text]
}

object Command {

  def reply(
      code: Int,
      parameters: Seq[Parameter] = Vector[Parameter](),
      text: Option[Text] = None): Command =
    NumericReply(code, parameters, text)

  def command(
      name: String,
      parameters: Seq[Parameter] = Vector[Parameter](),
      text: Option[Text] = None): Command =
    WordCommand(name, parameters, text)

  private[protocol] final case class NumericReply(
      code: Int,
      parameters: Seq[Parameter],
      text: Option[Text]) extends Command {
    def name: String = code.toString
  }

  private[protocol] final case class WordCommand(
      name: String,
      parameters: Seq[Parameter],
      text: Option[Text]) extends Command

}

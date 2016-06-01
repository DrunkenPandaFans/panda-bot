package sk.drunkenpanda.bot.plugins.calc

import scala.util.matching.Regex

import sk.drunkenpanda.bot.{Message, PrivateMessage, Response}
import sk.drunkenpanda.bot.plugins.Plugin

class Calculator {

  def evaluate(exp: Expression): BigDecimal = exp match {
    case Number(value) => value
    case UnaryOperator("-", exp) => -evaluate(exp)
    case BinaryOperator("+", left, right) => evaluate(left) + evaluate(right)
    case BinaryOperator("-", left, right) => evaluate(left) - evaluate(right)
    case BinaryOperator("*", left, right) => evaluate(left) * evaluate(right)
    case BinaryOperator("/", left, right) => evaluate(left) / evaluate(right)
    case _ => throw new IllegalArgumentException("Expression is not valid")
  }
}

class CalculatorPlugin(calculator: Calculator, parser: ExpressionParser) extends Plugin {

  private lazy val format: Regex = "panda compute ([\\W\\d ]+?),* please".r

  def respond(message: Message): Option[Message] = message match {
    case PrivateMessage(from, text) => prepareResponse(text).map(Response(from, _))
    case _ => None
  }

  def prepareResponse(text: String): Option[String] = text match {
    case format(expression) => Some(s"And your result is... " + process(expression) + "!!")
    case _ => None
  }

  def process: String => BigDecimal = parser.parse _ andThen calculator.evaluate _

  def onShutdown(): Unit = ()
}

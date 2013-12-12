package sk.drunkenpanda.bot.plugins.calc

import sk.drunkenpanda.bot.plugins.Plugin
import sk.drunkenpanda.bot.{Response, PrivateMessage, Message}

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

  private lazy val format = "panda compute ([\\W\\d ]+?),* please".r

  def respond(message: Message): Option[Message] = message match {
    case PrivateMessage(from, text) => Some(Response(from, prepareResponse(text)))
    case _ => None
  }

  def prepareResponse(text: String) = text match {
    case format(expression) => s"And your result is... " + process(expression) + "!!"
    case _ => "I am sorry, sir. But your expression is invalid."
  }

  def process(exp: String) = parser.parse _ andThen calculator.evaluate _
}

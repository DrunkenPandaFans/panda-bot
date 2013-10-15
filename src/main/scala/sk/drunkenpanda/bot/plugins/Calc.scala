package sk.drunkenpanda.bot.plugins

import sk.drunkenpanda.bot.Message

sealed trait Expression
case class Number(value: Double) extends Expression
case class UnaryOperator(value: String, exp: Expression) extends Expression
case class BinaryOperator(op: String, leftExp: Expression, rightExp: Expression) 
  extends Expression

class ExpressionEvaluator {
  def evaluate(exp: Expression): Double = exp match {
    case Number(value) => value
    case UnaryOperator("-", exp) => -evaluate(exp)
    case BinaryOperator("+", left, right) => evaluate(left) + evaluate(right)
    case BinaryOperator("-", left, right) => evaluate(left) - evaluate(right)
    case BinaryOperator("*", left, right) => evaluate(left) * evaluate(right)
    case BinaryOperator("/", left, right) => evaluate(left) / evaluate(right)
  }
}

class Calc extends Plugin {
  def respond(message: Message): Option[Message] = None
}

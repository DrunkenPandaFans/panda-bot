package sk.drunkenpanda.bot.plugins.calc

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

package sk.drunkenpanda.bot.plugins.calc

import org.parboiled.scala._
import org.parboiled.errors.{ErrorUtils, ParsingException}

class ExpressionParser extends Parser {

  def inputLine = rule { expression ~ EOI }

  def expression: Rule1[Expression] = rule { 
    expression ~ optional((addition | substraction) ~ term)
  }

  def term: Rule1[Expression] = rule {
    term ~ optional((multiplication | division | power) ~ factor)
  }

  def factor: Rule1[Expression] = rule {
    brackets | negation | number
  }

  def addition: Rule1[Expression] = rule { 
    expression ~ "+" ~ expression ~~> binaryOperation("+") _
  }

  def substraction: Rule1[Expression] = rule {
    expression ~ "-" ~ expression ~~> binaryOperation("-") _
  }

  def multiplication: Rule1[Expression] = rule {
    expression ~ "*" ~ expression ~~> binaryOperation("*") _
  }

  def division = rule {
    expression ~ "/" ~ expression ~~> binaryOperation("/") _
  }

  def power = rule {
    expression ~ "^" ~ expression ~~> binaryOperation("^") _
  }

  def negation = rule {
    "-" ~ expression ~~> unaryOperation("-") _
  }

  def brackets = rule { "(" ~ expression ~ ")" }

  def number = rule { 
    digits ~ optional("." ~ digits) ~> {s: String => Number(BigDecimal(s))}
  }

  def digits = rule { oneOrMore(digit) }

  def digit = rule { "0" - "9"}

  private def binaryOperation(symbol: String)(left: Expression, right: Expression) =
    BinaryOperator(symbol, left, right)

  private def unaryOperation(symbol: String)(expr: Expression): Expression = 
    UnaryOperator(symbol, expr)

  def calculate(value: String): Expression = {
    val parsingResult = ReportingParseRunner(inputLine).run(value)
    parsingResult.result match {
      case Some(expr) => expr
      case None => throw new ParsingException("Invalid calculation\n" +
        ErrorUtils.printParseErrors(parsingResult))
    }
  }
}

package sk.drunkenpanda.bot.plugins.calc

import org.parboiled.scala._
import org.parboiled.errors.{ErrorUtils, ParsingException}

class ExpressionParser extends Parser {

  def InputLine = rule { Input ~ EOI }

  def Input: Rule1[Expression] = Term ~ zeroOrMore(
    "+" ~ Term ~~> binaryOperator("+") _ |
    "-" ~ Term ~~> binaryOperator("-") _)

  def Term = rule {
    Factor ~ zeroOrMore(
      "*" ~ Factor ~~> binaryOperator("*") _ |
      "/" ~ Factor ~~> binaryOperator("/") _ |
      "^" ~ Factor ~~> binaryOperator("^") _
    )
  }

  def NegativeFraction = rule {"-" ~ Fraction ~~> (a => new UnaryOperator("-", a))}

  def Factor = rule { Fraction | Parents | NegativeFraction}

  def Parents = rule {"(" ~ Input ~ ")"}

  def Fraction = rule {
    group(oneOrMore("1" - "9") ~ optional("." ~ oneOrMore(Digits))) ~> ((value: String) => number(value))
  }

  def Digits = rule { oneOrMore("0" - "9") }

  def number(value: String) = Number(BigDecimal(value))

  def binaryOperator(symbol: String)(a: Expression, b: Expression): Expression =
    BinaryOperator(symbol, a, b)

  def parse(value: String) = {
    val parsingResult = ReportingParseRunner(InputLine).run(value)
    parsingResult.result match {
      case Some(expr) => expr
      case None => throw new ParsingException("Invalid input value.\n" + ErrorUtils.printParseErrors(parsingResult))
    }
  }
}

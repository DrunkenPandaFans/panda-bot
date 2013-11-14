package sk.drunkenpanda.bot.plugins.calc

import org.parboiled.scala._
import org.parboiled.errors.{ErrorUtils, ParsingException}

class ExpressionParser extends Parser {

  private def InputLine = rule { Input ~ EOI }

  private def Input: Rule1[Expression] = Term ~ zeroOrMore(
    "+" ~ Term ~~> binaryOperator("+") _ |
      "-" ~ Term ~~> binaryOperator("-") _)

  private def Term = rule {
    Factor ~ zeroOrMore(
      "*" ~ Factor ~~> binaryOperator("*") _ |
      "/" ~ Factor ~~> binaryOperator("/") _ |
      "^" ~ Factor ~~> binaryOperator("^") _
    )
  }

  private def NegativeFraction = rule {"-" ~ Fraction ~~> (a => UnaryOperator("-", a))}

  private def Factor = rule { Fraction | Parents | NegativeFraction}

  private def Parents = rule {"(" ~ Input ~ ")"}

  private def Fraction = rule {
    group(oneOrMore("1" - "9") ~
      optional("." ~ oneOrMore(Digits))) ~>
    ((value: String) => Number(BigDecimal(value)))
  }

  private def Digits = rule { oneOrMore("0" - "9") }

  private def binaryOperator(symbol: String)(a: Expression, b: Expression): Expression =
    BinaryOperator(symbol, a, b)

  def parse(value: String) = {
    val parsingResult = ReportingParseRunner(InputLine).run(value)
    parsingResult.result match {
      case Some(expr) => expr
      case None => throw new ParsingException("Invalid input value.\n" + ErrorUtils.printParseErrors(parsingResult))
    }
  }
}

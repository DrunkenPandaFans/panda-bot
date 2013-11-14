package sk.drunkenpanda.bot.plugins.calc

import org.parboiled.scala._
import org.parboiled.errors.{ErrorUtils, ParsingException}

class ExpressionParser extends Parser {

  def InputLine = rule { InputExpression ~ EOI }

  def InputExpression: Rule1[Expression] = rule {
    Term ~ zeroOrMore (
      "+" ~ Term ~~> binaryOperator("+") _
    | "-" ~ Term ~~> binaryOperator("-") _
    )
  }

  def Term = rule {
    Factor ~ zeroOrMore(
      "*" ~ Factor ~~> binaryOperator("*")
    | "/" ~ Factor ~~> binaryOperator("/")
    | "^" ~ Facor ~~> binaryOperator("^")
    )
  }

  def Factor = rule { DecimalNumber | Brackets }

  def Brackets = rule { "(" ~ InputExpression ~ ")" }

  def DecimalNumber = rule { 
    group(("1" - "9") ~ optional("." ~ Digits)) ~> number
  }

  def Digits = rule { oneOrMore("0" - "9") }

  def calculate(value: String): Expression = {
    val parsingResult = ReportingParseRunner(InputLine).run(value)
    parsingResult.result match {
      case Some(expr) => expr
      case None => throw new ParsingException("Invalid calculation\n" +
        ErrorUtils.printParseErrors(parsingResult))
    }
  }

  private def binaryOperator(symbol: String)(a: Expression, b: Expression) = 
    new BinaryOperator(symbol, a, b)

  private def number(value: String) = new Number(BigDecimal(value))
}

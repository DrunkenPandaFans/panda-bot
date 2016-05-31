package sk.drunkenpanda.bot.plugins.calc

import org.parboiled.errors.ParsingException
import org.scalatest.{ Matchers, FlatSpec }

class ExpressionParserSpecs extends FlatSpec with Matchers {

  val parser = new ExpressionParser

  behavior of "ExpressionParser"

  it should "parse simple numbers as Number" in {
    parser.parse("1") should equal(Number("1"))
    parser.parse("1.2") should equal(Number("1.2"))
  }

  it should "parse negative numbers as mix of UnaryOperator and Number" in {
    val expected1 = UnaryOperator("-", Number("1"))
    parser.parse("-1") should equal(expected1)
    val expected2 = UnaryOperator("-", Number("12.3"))
    parser.parse("-12.3") should equal(expected2)
  }

  it should "throw ParsingException, when pasing numbers, that starts with zero" in {
    intercept[ParsingException] {
      parser.parse("012345")
    }
  }

  it should "throw ParsingException, when parsing invalid decimal number" in {
    List("12.", "asd.12", "12.ads", ".12").map { n =>
      intercept[ParsingException] {
        parser.parse(n)
      }
    }
  }

  it should "parse decimal number with zero in decimal part" in {
    parser.parse("1.023") should equal(Number("1.023"))
  }

  it should "parse expression in parenthesis" in {
    val expected = BinaryOperator("+", Number("1"), Number("2"))
    parser.parse("(1+2)") should equal(expected)
  }

  it should "parse '*' as BinaryOperator" in {
    val expected = BinaryOperator("*", Number("1.0"), Number("2.3"))
    parser.parse("1.0*2.3") should equal(expected)
  }

  it should "parse '/' as BinaryOperator" in {
    val expected = BinaryOperator("/", Number("1.0"), Number("3.4"))
    parser.parse("1.0/3.4") should equal(expected)
  }

  it should "parse '^' as BinaryOperator" in {
    val expected = BinaryOperator("^", Number("2.0"), Number("3.0"))
    parser.parse("2.0^3.0") should equal(expected)
  }

  it should "parse '+' as BinaryOperator" in {
    val expected = BinaryOperator("+", Number(1), Number(2))
    parser.parse("1+2") should equal(expected)
  }

  it should "parse '-' as BinaryOperator" in {
    val expected = BinaryOperator("-", Number(1), Number(2))
    parser.parse("1-2") should equal(expected)
  }
}

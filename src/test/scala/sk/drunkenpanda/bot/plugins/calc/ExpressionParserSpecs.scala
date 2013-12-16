package sk.drunkenpanda.bot.plugins.calc

import org.parboiled.errors.ParsingException
import org.specs2.mutable._

class ExpressionParserSpecs extends Specification {

  val parser = new ExpressionParser

  "ExpressionParser" should {
    "parse simple numbers as Number" in {
      parser.parse("1") must beEqualTo(Number("1"))
      parser.parse("1.2") must beEqualTo(Number("1.2"))
    }

    "parse negative numbers as mix of UnaryOperator and Number" in {
      val expected1 = UnaryOperator("-", Number("1"))
      parser.parse("-1") must beEqualTo(expected1)
      val expected2 = UnaryOperator("-", Number("12.3"))
      parser.parse("-12.3") must beEqualTo(expected2)
    }

    "throw ParsingException, when pasing numbers, that starts with zero" in {
      parser.parse("012345") must throwA[ParsingException]
    }

    "throw ParsingException, when parsing invalid decimal number" in {
      parser.parse("12.") must throwA[ParsingException]
      parser.parse("asd.12") must throwA[ParsingException]
      parser.parse("12.ads") must throwA[ParsingException]
      parser.parse(".12") must throwA[ParsingException]
    }

    "parse decimal number with zero in decimal part" in {
      parser.parse("1.023") must beEqualTo(Number("1.023"))
    }

    "parse expression in parenthesis" in {
      val expected = BinaryOperator("+", Number("1"), Number("2"))
      parser.parse("(1+2)") must beEqualTo(expected)
    }

    "parse '*' as BinaryOperator" in {
      val expected = BinaryOperator("*", Number("1.0"), Number("2.3"))
      parser.parse("1.0*2.3") must beEqualTo(expected)
    }

    "parse '/' as BinaryOperator"  in {
      val expected = BinaryOperator("/", Number("1.0"), Number("3.4"))
      parser.parse("1.0/3.4") must beEqualTo(expected)
    }

    "parse '^' as BinaryOperator" in {
      val expected = BinaryOperator("^", Number("2.0"), Number("3.0"))
      parser.parse("2.0^3.0") must beEqualTo(expected)
    }

    "parse '+' as BinaryOperator" in {
      val expected = BinaryOperator("+", Number(1), Number(2))
      parser.parse("1+2") must beEqualTo(expected)
    }

    "parse '-' as BinaryOperator" in {
      val expected = BinaryOperator("-", Number(1), Number(2))
      parser.parse("1-2") must beEqualTo(expected)
    }
  }
}

package sk.drunkenpanda.bot.plugins.calc

import org.specs2.mutable._

class CalculatorSpecs extends Specification {

  val calculator = new Calculator()

  "Calculator" should {
    "evaluate Number expression to its value" in {
      calculator.evaluate(new Number(2.3)) must beEqualTo(BigDecimal(2.3))
    }

    "evaluate negation to negative of its value" in {
      val positiveValue = BigDecimal(1.2)
      val negativeValue = BigDecimal(-1.2)
      val negatePositive = new UnaryOperator("-", new Number(positiveValue))
      val negateNegative = new UnaryOperator("-", new Number(negativeValue))
      calculator.evaluate(negatePositive) must beEqualTo(negativeValue)
      calculator.evaluate(negateNegative) must beEqualTo(positiveValue)
    }

    "evaluate addition to sum of its left and right expression" in {
      val exp = new BinaryOperator("+", new Number(1.2), new Number(2.3))
      calculator.evaluate(exp) must beEqualTo(BigDecimal(3.5))
    }

    "evaluate subtraction to value of left side minus value of right side" in {
        val exp = new BinaryOperator("-", new Number(1.2), new Number(1.0))
        calculator.evaluate(exp) must beEqualTo(BigDecimal(0.2))
      }

    "evaluate multiplication to value of left side multiplied by right side" in {
      val exp = new BinaryOperator("*", new Number(2.0), new Number(1.3))
      calculator.evaluate(exp) must beEqualTo(BigDecimal(2.6))
    }

    "evaluate division to value of left side divided by right side" in {
      val exp = new BinaryOperator("/", new Number(6.0), new Number(2.0))
      calculator.evaluate(exp) must beEqualTo(BigDecimal(3.0))
    }

    "evaluate other binary operators to exception" in {
      val exp = new BinaryOperator("myoperator", new Number(6.0), new Number(2.0))
      calculator.evaluate(exp) must throwA[IllegalArgumentException]
    }

    "evaluate other unary operators to exception" in {
      val exp = new UnaryOperator("unaryoperator", new Number(6.0))
      calculator.evaluate(exp) must throwA[IllegalArgumentException]
    }
  }
}

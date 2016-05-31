package sk.drunkenpanda.bot.plugins.calc

import org.scalatest.{Matchers, FlatSpec}

class CalculatorSpecs extends FlatSpec with Matchers {

  val calculator = new Calculator()

  behavior of "Calculator"
  
    it should "evaluate Number expression to its value" in {
      calculator.evaluate(new Number(2.3)) should equal(BigDecimal(2.3))
    }

    it should "evaluate negation to negative of its value" in {
      val positiveValue = BigDecimal(1.2)
      val negativeValue = BigDecimal(-1.2)
      val negatePositive = new UnaryOperator("-", new Number(positiveValue))
      val negateNegative = new UnaryOperator("-", new Number(negativeValue))
      calculator.evaluate(negatePositive) should equal(negativeValue)
      calculator.evaluate(negateNegative) should equal(positiveValue)
    }

    it should "evaluate addition to sum of its left and right expression" in {
      val exp = new BinaryOperator("+", new Number(1.2), new Number(2.3))
      calculator.evaluate(exp) should equal(BigDecimal(3.5))
    }

    it should "evaluate subtraction to value of left side minus value of right side" in {
        val exp = new BinaryOperator("-", new Number(1.2), new Number(1.0))
        calculator.evaluate(exp) should equal(BigDecimal(0.2))
      }

    it should "evaluate multiplication to value of left side multiplied by right side" in {
      val exp = new BinaryOperator("*", new Number(2.0), new Number(1.3))
      calculator.evaluate(exp) should equal(BigDecimal(2.6))
    }

    it should "evaluate division to value of left side divided by right side" in {
      val exp = new BinaryOperator("/", new Number(6.0), new Number(2.0))
      calculator.evaluate(exp) should equal(BigDecimal(3.0))
    }

    it should "evaluate other binary operators to exception" in {
      val exp = new BinaryOperator("myoperator", new Number(6.0), new Number(2.0))
      intercept[IllegalArgumentException] {
        calculator.evaluate(exp)
      }
    }

    it should "evaluate other unary operators to exception" in {
      val exp = new UnaryOperator("unaryoperator", new Number(6.0))
      intercept[IllegalArgumentException] {
        calculator.evaluate(exp)
      }
    }
}

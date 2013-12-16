package sk.drunkenpanda.bot.plugins.calc

import org.specs2.mutable._
import org.specs2.mock._
import sk.drunkenpanda.bot.Message
import sk.drunkenpanda.bot.Notice
import sk.drunkenpanda.bot.Ping
import sk.drunkenpanda.bot.Pong
import sk.drunkenpanda.bot.PrivateMessage
import sk.drunkenpanda.bot.Response
import sk.drunkenpanda.bot.Unknown

class CalculatorPluginSpecs extends Specification with Mockito {

  val calculatorMock = mock[Calculator]
  val expressionParserMock = mock[ExpressionParser]
  val plugin = new CalculatorPlugin(calculatorMock, expressionParserMock)

  "CalculatorPlugin" should {

    "not respond to message other than PrivateMessage" in {
      plugin.respond(Ping("hash")) must beNone
      plugin.respond(Pong("hash")) must beNone
      plugin.respond(Notice("extremely important notice")) must beNone
      plugin.respond(Response("octocat", "Hi, Octocat!")) must beNone
      plugin.respond(Unknown) must beNone
    }

    "respond to PrivateMessage" in {
      plugin.respond(PrivateMessage("octocat", "1+2")) must beSome[Message]
    }

    "prepare info about invalid format if text is invalid" in {
      plugin.prepareResponse("compute 1+2, please") must startWith("I am sorry, sir")
      plugin.prepareResponse("compute 2+1") must startWith("I am sorry, sir")
    }

    "prepare results if text is in valid format" in {
      val expression = BinaryOperator("+", Number(1.0), Number(2.0))
      calculatorMock.evaluate(expression) returns BigDecimal(3.0)
      expressionParserMock.parse("1+2") returns expression
      
      val expected = "And your result is... 3.0!!"
      plugin.prepareResponse("panda compute 1+2, please") must beEqualTo(expected)
    }
  }
}

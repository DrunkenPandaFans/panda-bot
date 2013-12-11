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
  }
}

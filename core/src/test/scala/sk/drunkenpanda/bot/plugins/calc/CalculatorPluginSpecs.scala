package sk.drunkenpanda.bot.plugins.calc

import org.scalatest.mockito.MockitoSugar
import org.scalatest.{ Matchers, FlatSpec }
import org.mockito.Mockito._
import sk.drunkenpanda.bot.Notice
import sk.drunkenpanda.bot.Ping
import sk.drunkenpanda.bot.Pong
import sk.drunkenpanda.bot.PrivateMessage
import sk.drunkenpanda.bot.Response
import sk.drunkenpanda.bot.Unknown

class CalculatorPluginSpecs extends FlatSpec with Matchers with MockitoSugar {

  val calculatorMock = mock[Calculator]
  val expressionParserMock = mock[ExpressionParser]
  val plugin = new CalculatorPlugin(calculatorMock, expressionParserMock)

  behavior of "CalculatorPlugin"

  it should "not respond to message other than PrivateMessage" in {
    plugin.respond(Ping("hash")) should not be defined
    plugin.respond(Pong("hash")) should not be defined
    plugin.respond(Notice("extremely important notice")) should not be defined
    plugin.respond(Response("octocat", "Hi, Octocat!")) should not be defined
    plugin.respond(Unknown) should not be defined
  }

  it should "respond to PrivateMessage" in {
    plugin.respond(PrivateMessage("octocat", "panda compute 1+2, please")) shouldBe defined
  }

  it should "prepare info about invalid format if text is invalid" in {
    plugin.prepareResponse("compute 1+2, please") should not be defined
    plugin.prepareResponse("compute 2+1") should not be defined
  }

  it should "prepare results if text is in valid format" in {
    val expression = BinaryOperator("+", Number(1.0), Number(2.0))
    when(calculatorMock.evaluate(expression)).thenReturn(BigDecimal(3.0))
    when(expressionParserMock.parse("1+2")).thenReturn(expression)

    val expected = "And your result is... 3.0!!"
    plugin.prepareResponse("panda compute 1+2, please") shouldBe Some(expected)
  }
}

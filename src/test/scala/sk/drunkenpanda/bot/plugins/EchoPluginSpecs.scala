package sk.drunkenpanda.bot.plugins

import org.scalatest.{Matchers, FlatSpec}
import sk.drunkenpanda.bot.Notice
import sk.drunkenpanda.bot.Ping
import sk.drunkenpanda.bot.Pong
import sk.drunkenpanda.bot.PrivateMessage
import sk.drunkenpanda.bot.Response
import sk.drunkenpanda.bot.Unknown

class EchoPluginSpecs extends FlatSpec with Matchers {

  val echoPlugin = new EchoPlugin()

  behavior of "EchoPlugin"

    it should "respond to private messages, that start with 'panda echo'" in {
      val message = new PrivateMessage("#mychannel", "panda echo this is awesome test, please.")
      val expectedResponse = new Response("#mychannel",
        "Echoing message...this is awesome test")

      echoPlugin.respond(message) shouldBe Some(expectedResponse)
    }

    it should "not respond to other messages than private" in {
      echoPlugin.respond(Unknown) should not be defined
      echoPlugin.respond(new Ping("pingpong")) should not be defined
      echoPlugin.respond(new Pong("pongping")) should not be defined
      echoPlugin.respond(new Notice("note")) should not be defined
    }

    it should "parse text, that starts with 'panda echo' and end with ', please'" in {
      val message = "panda echo this is awesome, please"
      echoPlugin.parseText(message) shouldBe Some("this is awesome")
    }

    it should "not parse text, that does not start with 'panda echo'" in {
      val message = "this is epic fail, please"
      echoPlugin.parseText(message) should not be defined
    }

    it should "not parse text, that does not end with ', please'" in {
      val message = "panda echo this is even more epic fail"
      echoPlugin.parseText(message) should not be defined
    }

    it should "prepare message if there is some text" in {
      val text = "this is awesome message, it has to be sent."
      echoPlugin.prepareResponse("toMe", Some(text)) shouldBe Some(
        new Response("toMe", s"Echoing message...$text"))
    }

    it should "not prepare message if there is no text" in {
      echoPlugin.prepareResponse("toMe", None) should not be defined
    }

}

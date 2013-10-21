package sk.drunkenpanda.bot.plugins

import org.specs2.mutable._
import sk.drunkenpanda.bot.Notice
import sk.drunkenpanda.bot.Ping
import sk.drunkenpanda.bot.Pong
import sk.drunkenpanda.bot.PrivateMessage
import sk.drunkenpanda.bot.Response
import sk.drunkenpanda.bot.Unknown

/**
 * Created with IntelliJ IDEA.
 * User: ferko
 * Date: 10/18/13
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
class EchoPluginSpecs extends Specification {

  val echoPlugin = new EchoPlugin()

  "EchoPlugin" should {
    "respond to private messages, that start with 'panda echo'" in {
       val message = new PrivateMessage("#mychannel", "panda echo this is awesome test, please.")
       val expectedResponse = new Response("#mychannel",
        "Echoing message...this is awesome test")

        echoPlugin.respond(message) must beSome(expectedResponse)
      }

      "not respond to other messages than private" in {
        echoPlugin.respond(Unknown) must beNone
        echoPlugin.respond(new Ping("pingpong")) must beNone
        echoPlugin.respond(new Pong("pongping")) must beNone
        echoPlugin.respond(new Notice("note")) must beNone
      }

      "parse text, that starts with 'panda echo' and end with ', please'" in {
        val message = "panda echo this is awesome, please"
        echoPlugin.parseText(message) must beSome("this is awesome")
      }

      "not parse text, that does not start with 'panda echo'" in {
        val message = "this is epic fail, please"
        echoPlugin.parseText(message) must beNone
      }

      "not parse text, that does not end with ', please'" in {
        val message = "panda echo this is even more epic fail"
        echoPlugin.parseText(message) must beNone
      }

      "prepare message if there is some text" in {
        val text = "this is awesome message, it has to be sent."
        echoPlugin.prepareResponse("toMe", Some(text)) must beSome(
          new Response("toMe", s"Echoing message...$text"))
      }

      "not prepare message if there is no text" in {
        echoPlugin.prepareResponse("toMe", None) must beNone
      }
  }

}

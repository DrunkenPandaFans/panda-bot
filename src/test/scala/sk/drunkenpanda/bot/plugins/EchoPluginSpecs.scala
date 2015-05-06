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
       val message = new PrivateMessage("#mychannel", "panda echo this is awesome test")
       val expectedResponse = new Response("#mychannel",
        "this is awesome testttt")

        echoPlugin.respond(message) must beSome(expectedResponse)
      }

      "not respond to other messages than private" in {
        echoPlugin.respond(Unknown) must beNone
        echoPlugin.respond(new Ping("pingpong")) must beNone
        echoPlugin.respond(new Pong("pongping")) must beNone
        echoPlugin.respond(new Notice("note")) must beNone
      }

      "parse text, that starts with 'panda echo'" in {
        val message = "panda echo this is awesome"
        echoPlugin.parseText(message) must beSome(("this is awesome", ""))
      }

      "parse text with suffix" in {
        val message = "panda echo this is awesome!!"
        echoPlugin.parseText(message) must beSome(("this is awesome", "!!"))
      }

      "not parse text, that does not start with 'panda echo'" in {
        val message = "this is epic fail, please"
        echoPlugin.parseText(message) must beNone
      }

      "prepare message if there is some text" in {
        val text = "this is awesome message, it has to be sent"
        echoPlugin.prepareResponse("toMe", text, "") must beEqualTo(
          Response("toMe", s"${text}ttt"))
      }

      "prepare message with suffix" in {
        val message = "this is awesome message"
        echoPlugin.prepareResponse("toMe", message, "!!") must beEqualTo(
          Response("toMe", "this is awesome messageeee!!"))
      }
  }

}

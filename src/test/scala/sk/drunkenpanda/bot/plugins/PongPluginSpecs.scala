package sk.drunkenpanda.bot.plugins

import org.specs2.mutable._
import sk.drunkenpanda.bot.Notice
import sk.drunkenpanda.bot.Ping
import sk.drunkenpanda.bot.Pong
import sk.drunkenpanda.bot.PrivateMessage
import sk.drunkenpanda.bot.Response
import sk.drunkenpanda.bot.Unknown

class PongPluginSpecs extends Specification {

  val pongPlugin = new PongPlugin()

  "PongPlugin" should {
    "respond to ping message" in {
      val hash = "abcde1234"
      pongPlugin.respond(new Ping(hash)) must beSome(new Pong(hash))
    }

    "not respond to other type of messages" in {
      pongPlugin.respond(Unknown) must beNone
      pongPlugin.respond(new Notice("Message")) must beNone
      val privMessage = new PrivateMessage("noone", "This is weird msg")
      pongPlugin.respond(privMessage) must beNone
      pongPlugin.respond(new Response("noo", "this is strange")) must beNone
      pongPlugin.respond(new Pong("ponging")) must beNone
    }
  }
}

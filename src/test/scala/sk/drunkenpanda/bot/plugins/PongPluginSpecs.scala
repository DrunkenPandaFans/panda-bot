package sk.drunkenpanda.bot.plugins

import org.scalatest.{Matchers, FlatSpec}
import sk.drunkenpanda.bot.Notice
import sk.drunkenpanda.bot.Ping
import sk.drunkenpanda.bot.Pong
import sk.drunkenpanda.bot.PrivateMessage
import sk.drunkenpanda.bot.Response
import sk.drunkenpanda.bot.Unknown

class PongPluginSpecs extends FlatSpec with Matchers {

  val pongPlugin = new PongPlugin()

  behavior of "PongPlugin"

    it should "respond to ping message" in {
      val hash = "abcde1234"
      pongPlugin.respond(new Ping(hash)) shouldEqual Some(new Pong(hash))
    }

    it should "not respond to other type of messages" in {
      pongPlugin.respond(Unknown) should not be defined
      pongPlugin.respond(new Notice("Message")) should not be defined
      val privMessage = new PrivateMessage("noone", "This is weird msg")
      pongPlugin.respond(privMessage) should not be defined
      pongPlugin.respond(new Response("noo", "this is strange")) should not be defined
      pongPlugin.respond(new Pong("ponging")) should not be defined
    }

}

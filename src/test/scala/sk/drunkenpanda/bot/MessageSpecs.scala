package sk.drunkenpanda.bot

import org.specs2.mutable._

class MessageSpecs extends Specification {

  "Message" should {
    "parse private message" in {
      val text = "PRIVMSG panda :This is awesome message"
      val expected = new PrivateMessage("panda", "This is awesome message")
      Message.parse(text) must beEqualTo(expected)
    }

    "parse ping message" in {
      val text = "PING :abcde1234"
      val expected = new Ping("abcde1234")
      Message.parse(text) must beEqualTo(expected)
    }

    "parse notice message" in {
      val text = "NOTICE :Super important magic notice"
      val expected = new Notice("Super important magic notice")
      Message.parse(text) must beEqualTo(expected)
    }

    "not parse anything else" in {
      Message.parse("PONG :abcds") must beEqualTo(Unknown)
      Message.parse("something totally different") must beEqualTo(Unknown)
    }

    "print private message" in {
      val expected = "PRIVMSG toMe :This is awesome"
      val message = new Response("toMe", "This is awesome")
      Message.print(message) must beEqualTo(expected)
    }

    "print pong message" in {
      val expected = "PONG :abcde"
      val message = new Pong("abcde")
      Message.print(message) must beEqualTo(expected)
    }

    "print notice message" in {
      val expected = "NOTICE :my super notice"
      val message = new Notice("my super notice")
      Message.print(message) must beEqualTo(expected)
    }

    "not print anything else" in {
      val message = new Ping("abcds")
      Message.print(message) must beEqualTo("")
      Message.print(Unknown) must beEqualTo("")
      Message.print(new PrivateMessage("to", "private message")) must beEqualTo("")
    }
  }
}

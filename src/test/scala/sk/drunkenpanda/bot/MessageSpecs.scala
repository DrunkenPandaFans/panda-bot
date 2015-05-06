package sk.drunkenpanda.bot

import org.scalatest.{ShouldMatchers, FlatSpec}

class MessageSpecs extends FlatSpec with ShouldMatchers {

  behavior of "Message"

    it should "parse private message" in {
        val text = "PRIVMSG panda :This is awesome message"
        val expected = new PrivateMessage("panda", "This is awesome message")
        Message.parse(text) should equal(expected)
    }

    it should "parse ping message" in {
      val text = "PING :abcde1234"
      val expected = new Ping("abcde1234")
      Message.parse(text) should equal(expected)
    }

    it should "parse notice message" in {
      val text = "NOTICE :Super important magic notice"
      val expected = new Notice("Super important magic notice")
      Message.parse(text) should equal(expected)
    }

    it should "not parse anything else" in {
      Message.parse("PONG :abcds") should equal(Unknown)
      Message.parse("something totally different") should equal(Unknown)
    }

    it should "print private message" in {
      val expected = "PRIVMSG toMe :This is awesome"
      val message = new Response("toMe", "This is awesome")
      Message.print(message) should equal(expected)
    }

    it should "print pong message" in {
      val expected = "PONG :abcde"
      val message = new Pong("abcde")
      Message.print(message) should equal(expected)
    }

    it should "print notice message" in {
      val expected = "NOTICE :my super notice"
      val message = new Notice("my super notice")
      Message.print(message) should equal(expected)
    }

    it should "print join message" in {
      val expected = "JOIN #drunken_panda"
      val message = Join("#drunken_panda")
      Message.print(message) should equal(expected)
    }

    it should "print leave message" in {
      val expected = "PART #drunken_panda"
      val message = Leave("#drunken_panda")
      Message.print(message) should equal(expected)
    }

    it should "print user message" in {
      val expected = "USER drunken_panda 0 * :Drunken Panda Bot"
      val message = User("drunken_panda", "Drunken Panda Bot")
      Message.print(message) should equal(expected)
    }

    it should "print nick message" in {
      val expected = "NICK DrunkenPanda"
      val message = Nick("DrunkenPanda")
      Message.print(message) should equal(expected)
    }

    it should "not print anything else" in {
      val message = new Ping("abcds")
      Message.print(message) should equal("")
      Message.print(Unknown) should equal("")
      Message.print(new PrivateMessage("to", "private message")) should equal("")
    }
}

package sk.drunkenpanda.bot.io

import java.io.{IOException, BufferedReader}

import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import org.specs2.time.NoTimeConversions
import rx.lang.scala.schedulers.TestScheduler
import sk.drunkenpanda.bot.{Join, Message, Response}
import scala.concurrent.duration._

/**
 * @author Jan Ferko
 */
class IrcClientSpec extends Specification with Mockito with NoTimeConversions {

  "IrcClient" should {

    "send NICK message during connection" in new system {
      ircClient.connect("drunken_panda", "DrunkenPanda", "DrunkenPandaBot")
      there was one(mockConnectionSource).write("NICK DrunkenPanda")
    }

    "send USER message with user's name and real name during connection" in new system {
      ircClient.connect("drunken_panda", "DrunkenPanda", "DrunkenPandaBot")
      there was one(mockConnectionSource).write("USER drunken_panda 0 * :DrunkenPandaBot")
    }

    "write message value to source" in new system {
      val msg = Response("#drunkenpandas", "Hello from IrcClient spec... Everything is looking good")
      ircClient.write(msg)
      there was one(mockConnectionSource).write(Message.print(msg))
    }

    "shutdown source on client shutdown" in new system {
      ircClient.shutdown
      there was one(mockConnectionSource).shutdown
    }

    "connect to channels when listening" in new system {
      val channels = List("#drunkenpandas", "#scala", "#scalaz")
      val messages = ircClient.listen(channels).take(1).subscribe(println(_)).unsubscribe

      // expected messages sent to source
      channels.map(ch => Message.print(Join(ch))).foreach {msg =>
        there was one(mockConnectionSource).write(msg)
      }
    }

    //@TODO this does not test failures properly...
    "notifies subscribers on error" in new system {
      mockConnectionSource.read(any[Function1[BufferedReader, String]]) throws(new IllegalStateException())
      val testScheduler = TestScheduler()
      val messages = ircClient.listen(List("#drunkenpanda")).subscribeOn(testScheduler)
      testScheduler.advanceTimeBy(5 seconds)

      messages.subscribe(
        message => failure("Client should report errors to subscribers"),
        err => err must beAnInstanceOf[IllegalStateException]
      ).unsubscribe
    }
  }

  trait system extends Scope {
    val mockConnectionSource = mock[ConnectionSource]

    val ircClient = new IrcClient {
      override def source: ConnectionSource = mockConnectionSource
    }
  }
}

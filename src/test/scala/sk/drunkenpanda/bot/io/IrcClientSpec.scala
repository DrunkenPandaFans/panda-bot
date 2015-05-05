package sk.drunkenpanda.bot.io

import java.io.BufferedReader
import java.lang
import java.util.concurrent.{ExecutorService, Executors}

import org.mockito.Mockito._
import org.mockito.Matchers._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, ShouldMatchers, FlatSpec}
import rx.lang.scala.schedulers.TestScheduler
import sk.drunkenpanda.bot.{Join, Message, Response}

import scala.concurrent.duration._

/**
 * @author Jan Ferko
 */
class IrcClientSpec extends FlatSpec with ShouldMatchers with MockitoSugar with BeforeAndAfterEach {

  val mockConnectionSource = mock[ConnectionSource]

  val ircClient = new IrcClient {
    override def source: ConnectionSource = mockConnectionSource

    override val executor: ExecutorService = Executors.newSingleThreadExecutor
  }

  override protected def beforeEach(): Unit = {
    reset(mockConnectionSource)
  }

  behavior of "IrcClient"

    it should "send NICK message during connection" in {
      ircClient.connect("drunken_panda", "DrunkenPanda", "DrunkenPandaBot")
      verify(mockConnectionSource).write("NICK DrunkenPanda")
    }

    it should "send USER message with user's name and real name during connection" in {
      ircClient.connect("drunken_panda", "DrunkenPanda", "DrunkenPandaBot")
      verify(mockConnectionSource).write("USER drunken_panda 0 * :DrunkenPandaBot")
    }

    it should "write message value to source" in {
      val msg = Response("#drunkenpandas", "Hello from IrcClient spec... Everything is looking good")
      ircClient.write(msg)
      verify(mockConnectionSource).write(Message.print(msg))
    }

    it should "shutdown source on client shutdown" in {
      ircClient.shutdown
      verify(mockConnectionSource).shutdown
    }

    it should "connect to channels when listening" in {
      val channels = List("#drunkenpandas", "#scala", "#scalaz")
      val testScheduler = TestScheduler()
      val messages = ircClient.listen(channels).take(1).subscribeOn(testScheduler).subscribe(s => ()).unsubscribe
      testScheduler.advanceTimeBy(1 second)

      // expected messages sent to source
      channels.map(ch => Message.print(Join(ch))).foreach {msg =>
        verify(mockConnectionSource).write(msg)
      }
    }

    //@TODO this does not test failures properly...
    it should "notifies subscribers on error" in {
      when(mockConnectionSource.read(any(classOf[(BufferedReader) => String])))
        .thenThrow(new IllegalStateException())

      val testScheduler = TestScheduler()
      val messages = ircClient.listen(List("#drunkenpanda")).subscribeOn(testScheduler)
      testScheduler.advanceTimeBy(5 seconds)

      messages.subscribe(
        message => fail("Client should report errors to subscribers"),
        err => err shouldBe a [lang.IllegalStateException]
      ).unsubscribe
    }
}

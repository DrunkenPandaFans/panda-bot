package sk.drunkenpanda.bot.io

import java.util.concurrent.{Executors, ExecutorService}

import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{BeforeAndAfterEach, FlatSpec, ShouldMatchers}
import sk.drunkenpanda.bot.{Join, Message, Response}

import scala.util.{Failure, Success}

/**
 * @author Jan Ferko
 */
class IrcClientSpec extends FlatSpec with ShouldMatchers with MockitoSugar with BeforeAndAfterEach {

  val mockConnectionSource = mock[ConnectionSource]

  val ircClient = new IrcClient {
    override def source: ConnectionSource = mockConnectionSource

    override def executor: ExecutorService = Executors.newSingleThreadExecutor
  }

  override protected def beforeEach(): Unit = {
    reset(mockConnectionSource)
    when(mockConnectionSource.write(any[String])).thenReturn(Success(()))
    when(mockConnectionSource.shutdown).thenReturn(Success(()))
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
      val messages = ircClient.listen(channels).take(1).subscribe(s => ())

      // expected messages sent to source
      channels.map(ch => Message.print(Join(ch))).foreach { msg =>
        verify(mockConnectionSource).write(msg)
      }
      messages.unsubscribe
    }

    it should "notifies subscribers on error" in {
      //given
      val channels = List("#panda")
      var reported = false
      when(mockConnectionSource.read).thenReturn(Failure(new IllegalArgumentException))

      // when
      val sub = ircClient.listen(channels).subscribe(
        s => fail("Observable should report error"),
        err => {
          err shouldBe a [IllegalArgumentException]
          reported = true
        }
      )

      // then
      Thread.sleep(100)
      reported shouldBe true

      sub.unsubscribe
    }

    it should "notify subscribers when new message is received" in {
      // given
      val channels = List("#panda")
      var reported = false
      val message = "Message1"
      when(mockConnectionSource.read).thenReturn(Success(message))

      // when
      val sub = ircClient.listen(channels).take(1).subscribe(
        msg => {
          msg should equal(message)
          reported = true
        }
      )

      // then
      Thread.sleep(100)
      reported shouldBe true

      sub.unsubscribe
    }
}

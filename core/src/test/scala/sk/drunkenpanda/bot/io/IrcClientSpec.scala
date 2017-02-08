package sk.drunkenpanda.bot.io

import java.util.concurrent.{ Executors, ExecutorService }

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{ Failure, Success }

import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{ BeforeAndAfterEach, FlatSpec, Matchers }
import sk.drunkenpanda.bot.{ Join, Message, Response }

/**
 * @author Jan Ferko
 */
class IrcClientSpec extends FlatSpec with Matchers with MockitoSugar with BeforeAndAfterEach {

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
    when(mockConnectionSource.read).thenReturn(Success("Hello, friends!"))

    // when
    val messages = ircClient.listen(channels).take(1)

    // then
    Await.ready(messages.toBlocking.toFuture, 1.second)
    channels.map(ch => Message.print(Join(ch))).foreach { msg =>
      verify(mockConnectionSource).write(msg)
    }
  }

  it should "notifies subscribers on error" in {
    //given
    val channels = List("#panda")
    when(mockConnectionSource.read).thenReturn(Failure(new IllegalArgumentException))

    // when
    val messagesObservable = ircClient.listen(channels).take(1)
    intercept[IllegalArgumentException] {
      Await.result(messagesObservable.toBlocking.toFuture, 5.second)
    }
  }

  it should "notify subscribers when new message is received" in {
    // given
    val channels = List("#panda")
    val expectedMessage = "Hello, friends!"
    when(mockConnectionSource.read).thenReturn(Success(expectedMessage))

    // when
    val messages = ircClient.listen(channels).take(1)
    val actualMessage = Await.result(messages.toBlocking.toFuture, 1.second)
    actualMessage shouldBe expectedMessage
  }
}

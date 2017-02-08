package sk.drunkenpanda.bot.io

import java.io.{ ByteArrayOutputStream, ByteArrayInputStream }
import java.net.Socket

import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import org.scalatest.{ BeforeAndAfterEach, Matchers, FlatSpec }

class SocketConnectionSourceSpec extends FlatSpec with Matchers with MockitoSugar with BeforeAndAfterEach {

  val socket = mock[Socket]
  val socketSource = SocketConnectionSource(socket)

  override protected def beforeEach() = {
    reset(socket)
  }

  behavior of "SocketConnectionSource"

  it should "read and write messages to server" in {
    when(socket.getInputStream).thenReturn(new ByteArrayInputStream("PONG".getBytes))
    val outputStream = new ByteArrayOutputStream
    when(socket.getOutputStream).thenReturn(outputStream)

    socketSource.write("PING")

    val msg = socketSource.read

    msg.recover { case t => fail(t) }.foreach(_ shouldBe "PONG")
    outputStream.toString shouldBe "PING\n"
  }

  it should "report error while reading messages" in {
    when(socket.getInputStream).thenThrow(new IllegalStateException("Socket already closed"))

    intercept[IllegalStateException] {
      socketSource.read.get
    }
  }

  it should "report error while writing message" in {
    when(socket.getOutputStream).thenThrow(new IllegalStateException("Socket is already closed"))

    intercept[IllegalStateException] {
      socketSource.write("PING").get
    }
  }

}

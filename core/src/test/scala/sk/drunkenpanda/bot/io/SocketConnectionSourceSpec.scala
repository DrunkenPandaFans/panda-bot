package sk.drunkenpanda.bot.io

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream }
import java.net.Socket

import org.mockito.Mockito._
import org.scalatest.mock.MockitoSugar
import org.scalatest.{ FlatSpec, ShouldMatchers }

class SocketConnectionSourceSpec extends FlatSpec with ShouldMatchers with MockitoSugar {

  behavior of "SocketConnectionSource"

  it should "read and write messages to server" in {
    val socket = mock[Socket]
    when(socket.getInputStream).thenReturn(new ByteArrayInputStream("PONG".getBytes))
    val outputStream = new ByteArrayOutputStream
    when(socket.getOutputStream).thenReturn(outputStream)

    val socketSource = SocketConnectionSource(socket)

    socketSource.write("PING")

    val msg = socketSource.read

    msg.recover { case t => fail(t) }.foreach(_ shouldBe "PONG")
    outputStream.toString shouldBe "PING\n"
  }

  it should "report error while reading messages" in {
    val socket = mock[Socket]
    when(socket.getInputStream).thenThrow(new IllegalStateException("Socket already closed"))
    val socketSource = SocketConnectionSource(socket)

    intercept[IllegalStateException] {
      socketSource.read.get
    }
  }

  it should "report error while writing message" in {
    val socket = mock[Socket]
    when(socket.getOutputStream).thenThrow(new IllegalStateException("Socket is already closed"))
    val socketSource = SocketConnectionSource(socket)

    intercept[IllegalStateException] {
      socketSource.write("PING").get
    }
  }

}

package sk.drunkenpanda.bot

import java.util.concurrent.Executors

import org.scalatest.{ ShouldMatchers, FlatSpec }
import sk.drunkenpanda.bot.io.{ ConnectionSource, IrcClient }

import scala.util.{ Success, Try }

class BotSpecs extends FlatSpec with ShouldMatchers {

  class MockConnectionSource extends ConnectionSource {
    val buffer = collection.mutable.Buffer[String]()

    def write(value: String): Try[Unit] = {
      buffer += value
      Success(())
    }

    def read: Try[String] = Success("PONG")

    def shutdown: Try[Unit] = Success(())
  }

  class MockIrcClient extends IrcClient {
    override def source = new MockConnectionSource()
    override def executor = Executors.newSingleThreadExecutor()
  }

}

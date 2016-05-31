package sk.drunkenpanda.bot.io

import java.util.concurrent.{Executors, ExecutorService}

import rx.lang.scala.Observable
import sk.drunkenpanda.bot.{Join, Message}

import scala.util.Try

class IrcClient(source: ConnectionSource, executor: ExecutorService) {

  def connect(username: String, nickname: String, realName: String): Try[Unit] = for {
    _ <- source.write(s"NICK $nickname")
    _ <- source.write(s"USER $username 0 * :$realName")
  } yield ()

  def listen(channels: Seq[String]): Observable[String] = {
    channels.map(Join(_)).foreach(write(_))

    Observable[String](subscriber => {
      executor.execute(new Runnable {
        override def run(): Unit = {
          while (!subscriber.isUnsubscribed) {
            source.read.map { line =>
              subscriber.onNext(line)
            } recover { case e =>
              if (!subscriber.isUnsubscribed) {
                subscriber.onError(e)
              }
            }
          }

          if (!subscriber.isUnsubscribed) {
            subscriber.onCompleted
          }
        }
      })
    })
  }

  def write(message: Message): Try[Unit] = {
    val msg = Message.print(message)
    source.write(msg)
  }

  def shutdown: Try[Unit] = source.shutdown
}

object IrcClient {

  def apply(server: String, port: Int): IrcClient = {
    val socketConnectionSource = SocketConnectionSource(server, port)
    val executor = Executors.newFixedThreadPool(Runtime.getRuntime.availableProcessors + 2)
    new IrcClient(socketConnectionSource, executor)
  }
}
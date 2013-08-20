package sk.drunkenpanda.bot


import com.twitter.finagle.builder.ServerBuilder
import com.twitter.finagle.http.{Http, Response}
import com.twitter.finagle.Service
import com.twitter.util.Future
import java.net.InetSocketAddress
import java.net.Socket
import org.jboss.netty.handler.codec.http.{HttpRequest, HttpResponse}
import util.Properties

import sk.drunkenpanda.bot.io._
import sk.drunkenpanda.bot.plugins.AbstractPluginModule
import sk.drunkenpanda.bot.plugins.EchoPlugin
import sk.drunkenpanda.bot.plugins.PongPlugin

object App {

  val source = new SocketConnectionSource(new Socket("irc.freenode.net", 6667))

  val bot = new Bot(new NetworkIrcClient())

  def startBot(source: ConnectionSource) = {
    bot.connect("PandaBot", "Drunken Panda Bot", "#drunken-panda")(source)

    val messageStream = bot.listen()(source)
    val responds = messageStream map { msg => SimplePluginModule.process(msg)}
    responds foreach { r => bot.send(r)(source)}
  }
   
  def startServer(): Unit = {
    val port = Properties.envOrElse("PORT", "8080").toInt
    println("Starting on port:" + port)

    ServerBuilder().
      codec(Http()).
      name("Panda-Bot-Server").
      bindTo(new InetSocketAddress(port)).
      build(new InfoService)
    println("Started.")
  }
   
  def main(args: Array[String]): Unit = {
    startServer
    startBot(source) 
  }
  
}

object SimplePluginModule extends AbstractPluginModule {

  override val plugins = Set(new PongPlugin(), new EchoPlugin())
}

class InfoService extends Service[HttpRequest, HttpResponse] {
 
  def apply(req: HttpRequest): Future[HttpResponse] = {
    val response = Response()
    response.setStatusCode(200)
    response.setContentString("Come drown your sorrows to #drunken-panda@freenode")
    Future(response)
  }

}

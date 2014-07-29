package sk.drunkenpanda.bot

import java.io.{InputStream, Reader}
import java.util.Properties

import scala.io.Source


/**
 * @author Jan Ferko
 */
object Config {

  def fromProperties: String => Config =
    prepareSource andThen loadProperties andThen convertPropertiesToConfig

  def prepareSource: String => InputStream =
    path => getClass.getClassLoader.getResourceAsStream(path)

  def loadProperties: InputStream => Properties = { source =>
    val props = new Properties()
    props.load(source)
    props
  }

  def convertPropertiesToConfig: Properties => Config = { props =>
    val host = props.getProperty("host")
    val port = props.getProperty("port").toInt
    val channel = props.getProperty("channel")
    val nickname = props.getProperty("nickname")
    val realname = props.getProperty("realname")
    Config(host, port, channel, nickname, realname)
  }
}

case class Config(host: String, port: Int, channel: String, nickname: String, realname: String)

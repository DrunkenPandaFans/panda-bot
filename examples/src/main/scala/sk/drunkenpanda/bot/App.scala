package sk.drunkenpanda.bot

import sk.drunkenpanda.bot.io.IrcClient
import sk.drunkenpanda.bot.plugins.AbstractPluginModule
import sk.drunkenpanda.bot.plugins.EchoPlugin
import sk.drunkenpanda.bot.plugins.PongPlugin
import sk.drunkenpanda.bot.plugins.calc.{ Calculator, ExpressionParser, CalculatorPlugin }

object BotApp extends scala.App {

  val client = IrcClient("irc.freenode.net", 6667)
  val pluginModule = new SimplePluginModule
  val bot = new Bot(client, pluginModule)
  val subscription = bot.start("Drunken_Panda", "Drunken_Panda", "Drunken Panda Bot", List("#drunken_panda"))
  registerShutdownHook(bot)

  def registerShutdownHook(bot: Bot) = Runtime.getRuntime.addShutdownHook(new Thread() {
    override def run() = {
      subscription.unsubscribe
      bot.stop
    }
  })

  class SimplePluginModule extends AbstractPluginModule {

    override val plugins = Set(new PongPlugin(), new EchoPlugin(),
      new CalculatorPlugin(new Calculator, new ExpressionParser))
  }
}


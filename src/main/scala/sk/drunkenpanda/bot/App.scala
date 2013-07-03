package sk.drunkenpanda.bot

import sk.drunkenpanda.bot.action._

object App {

  val bot = new SocketIrcClient()

  def test(): Action[String] = for {
    opened <- bot.open("panda-bot", "Drunken Panda Bot")
    joined <- if (opened) for {
        _ <- bot.join("#drunken-panda")
      } yield true
      else pure(false)

  }
     
  def program(): ConnectionProvider => Unit = 
    cp => cp(test)
  :w
  def main(args: Array[String]): Unit = {
    val p = program
    p(ConnectionProvider.freenode)
  }
}

package sk.drunkenpanda.bot.action

import java.net.Socket

case class Action[A](f: Socket => A) {
  def apply(socket: Socket) = f(socket)

  def map[B](g: A => B): Action[B] = Action(s => g(f(s)))

  def flatMap[B](g: A => Action[B]): Action[B] = Action(s => g(f(s))(s))  
}

class Reader {
  def pure[A](a: A) = Action(s => a)
}


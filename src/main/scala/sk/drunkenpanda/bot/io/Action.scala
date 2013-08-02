package sk.drunkenpanda.bot.io

class Action[S, A](g: S => A) {

  def apply(s: S): A = g(s)

  def map[B](f: A => B): Action[S, B] = new Action(s => f(g(s)))

  def flatMap[B](f: A => Action[S, B]): Action[S, B] = new Action(s => f(g(s))(s))
}

object Action {
  implicit def pure[S, A](f: S => A): Action[S, A] = new Action(f)
}

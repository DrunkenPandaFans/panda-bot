package sk.drunkenpanda.bot.plugins

abstract class State[+A] {

  def isEmpty: Boolean

  def first: A

  def next: State[A]
}

case class Next[A](value: A, state: State[A]) extends State[A] {

  def isEmpty = false

  def first = value

  def next = state

}

case object Done extends State[Nothing] {
  def isEmpty = true

  def first = 
    throw new NoSuchElementException("There is no value in Done state") 

  def next = 
    throw new UnsupportedOperationException("Its done, man! No more states are left")

}

trait Plugin {
  def execute(msg: String): State[String]
}

class EchoPlugin extends Plugin {

  def execute(msg: String) = {
    val echoMsg = "Echoing..." + msg
    new Next(echoMsg, Done)
  }
}

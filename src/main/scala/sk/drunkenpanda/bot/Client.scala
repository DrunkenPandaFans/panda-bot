package sk.drunkenpanda.bot

trait IrcClient[S <: ConnectionSource] {
  def join(channel: String): S => Unit

  def open(realname: String, username: String): S => Unit

  def send(to: String, msg: String): S => Unit

  def receive(): S => String

  def leave(channel: String): S => Unit
  
}

class NetworkIrcClient extends IrcClient[SocketConnectionSource] {

  def join(channel: String) = 
    s => s.write(w => w.write("JOIN " + channel))

  def open(realname: String, username: String) =
    s => s.write(w => {
      w.write("NICK " + username + "\n")
      w.write("USER " + username + " 0 * :" + realname)})

  def send(to: String, msg: String) = 
    s => s.write(w => w.write("PRIVMSG " + to + " :" + msg))

  def receive() = s => s.read(r => r.readLine())

  def leave(channel: String) =  s => s.write(w => w.write("PART " + channel))
}

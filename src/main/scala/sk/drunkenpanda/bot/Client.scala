package sk.drunkenpanda.bot

trait IrcClient[S <: ConnectionSource] {
  def join(channel: String): S => Unit

  def open(realname: String, username: String): S => Unit

  def send(message: Message): S => Unit  

  def receive(): S => Message  

  def leave(channel: String): S => Unit
  
}

class NetworkIrcClient extends IrcClient[SocketConnectionSource] {

  def join(channel: String) = 
    s => s.write(w => w.write("JOIN " + channel))

  def open(realname: String, username: String) =
    s => s.write(w => {
      w.write("NICK " + username + "\n")
      w.write("USER " + username + " 0 * :" + realname)})

  def send(message: Message) = 
    s => s.write(w => w.write(Message.print(message)))

  def receive() = s => Message.parse(s.read(r => r.readLine()))

  def leave(channel: String) =  s => s.write(w => w.write("PART " + channel))
}

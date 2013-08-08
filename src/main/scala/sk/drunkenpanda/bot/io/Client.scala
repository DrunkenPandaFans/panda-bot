package sk.drunkenpanda.bot.io

import sk.drunkenpanda.bot.Message

trait IrcClient {
  def join(channel: String): ConnectionSource => Unit

  def open(realname: String, username: String): ConnectionSource => Unit

  def send(message: Message): ConnectionSource => Unit

  def receive(): ConnectionSource => Message

  def leave(channel: String): ConnectionSource => Unit
  
}

class NetworkIrcClient extends IrcClient {

  def join(channel: String) = 
    s => s.write(w => w.write("JOIN " + channel))

  def open(realname: String, username: String) = 
    {s => s.write(w => {
      w.write("NICK " + username + "\n")
      w.write("USER " + username + " 0 * :" + realname)})}

  def send(message: Message) = 
    s => s.write(w => w.write(Message.print(message)))

  def receive() = 
    s => Message.parse(s.read(r => r.readLine()))

  def leave(channel: String) = 
    s => s.write(w => w.write("PART " + channel))
}

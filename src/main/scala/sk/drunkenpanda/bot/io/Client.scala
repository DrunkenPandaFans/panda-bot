package sk.drunkenpanda.bot.io

import sk.drunkenpanda.bot.Message

trait IrcClient {
  def join(channel: String): Action[ConnectionSource, Unit]

  def open(realname: String, username: String): Action[ConnectionSource, Unit]

  def send(message: Message): Action[ConnectionSource, Unit]

  def receive(): Action[ConnectionSource, Message]

  def leave(channel: String): Action[ConnectionSource, Unit]
  
}

class NetworkIrcClient extends IrcClient {

  def join(channel: String) = 
    new Action({s => s.write(w => w.write("JOIN " + channel))})

  def open(realname: String, username: String) = 
    new Action({s => s.write(w => {
      w.write("NICK " + username + "\n")
      w.write("USER " + username + " 0 * :" + realname)})})

  def send(message: Message) = 
    new Action({s => s.write(w => w.write(Message.print(message)))})

  def receive() = 
    new Action(s => Message.parse(s.read(r => r.readLine())))

  def leave(channel: String) = 
    new Action(s => s.write(w => w.write("PART " + channel)))
}

package com.github.iref.pandabot.protocol

import com.github.iref.pandabot.PandaBotSpec

class MessageSpec extends PandaBotSpec("Message") {

  import Message._

  def decode(s: String): Message = ???

  it should "decode and encode to same message" in {
    forAll { (m: Message) =>
      decode(encode(m)) should be (m)
    }
  }

}

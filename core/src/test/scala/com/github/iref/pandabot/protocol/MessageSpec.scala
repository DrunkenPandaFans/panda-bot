package com.github.iref.pandabot.protocol

import com.github.iref.pandabot.PandaBotSpec

class MessageSpec extends PandaBotSpec("Message") {

  it should "decode and encode to same message" in {
    forAll { (m: Message) =>
      SimpleStringDecoder(Encoder(m)) should be (Some(m))
    }
  }

}

package com.github.iref.pandabot.protocol

import cats.data.Xor
import com.github.iref.pandabot.PandaBotSpec

class ParamSpec extends PandaBotSpec("Param") {

  it should "reject parameters with whitespace" in {
    val param = Param.validate("a b")
    param.isLeft should be(true)
  }

  it should "reject parameters that starts with ':'" in {
    val param = Param.validate(":bc")
    param.isLeft should be(true)
  }

  it should "accept alphanumeric string" in {
    val param = Param.validate("abcsdfds12345")
    param should be(Xor.right("abcsdfds12345"))
  }

  behavior of "Nickname"

  it should "reject value that doesn't start with letter" in {
    List("1abs", "[asd", "^asd", " asbc").foreach { value =>
      val param = Nickname.from(value)
      param.isLeft should be(true)
    }
  }

  it should "reject values that contain characters other than digits, letters, [,],^,{,},\\,`" in {
    val param = Nickname.from("a1[]^{}\\` \t\n")
    param.isLeft should be(true)
  }

  it should "accept values that contain only digits, letters, [, ], ^, {, }, \\, `" in {
    val param = Nickname.from("a1[]^{}\\`")
    param.isRight should be(true)
    param.foreach { nick =>
      nick.value should be("a1[]^{}\\`")
    }
  }

  behavior of "Username"

  it should "reject value that starts with colon (':')" in {
    val param = Username.from(":user")
    param.isLeft should be(true)
  }

  it should "reject value that contains whitespace characters" in {
    val param = Username.from("f o   ob\tar")
    param.isLeft should be(true)
  }

  it should "accept alphanumeric string" in {
    val param = Username.from("abc123xyz")
    param.isRight should be(true)
    param.foreach { username =>
      username.value should be("abc123xyz")
    }
  }

  behavior of "Channel"

  it should "reject values that don't start with & or #" in {
    val param = Channel.from("foo")
    param.isLeft should be(true)
  }

  it should "reject whitespace values" in {
    val param = Channel.from("#ab c")
    param.isLeft should be(true)
  }

  it should "accept alphanumeric string starts with # or &" in {
    val param = Channel.from("#abc")
    param.isRight should be(true)
    param.foreach { channel =>
      channel.value should be("#abc")
    }
  }

  behavior of "Hostname"

  it should "reject values ending with dot (.)" in {
    val param = Hostname.from("abc.")
    param.isLeft should be(true)
  }

  it should "reject values something else than letters, digits, hyphen or dot" in {
    val params = List(Hostname.from("abc. xyz-foo2"), Hostname.from("abc._xyz1"))
    params.foreach { hostname =>
      hostname.isLeft should be(true)
    }
  }

  it should "accept single label hostname" in {
    val param = Hostname.from("abc")
    param.isRight should be(true)
    param.foreach { hostname =>
      hostname.value should be("abc")
    }
  }

  it should "accept multilabel hostname" in {
    val param = Hostname.from("123.abc.foo-bar")
    param.isRight should be(true)
    param.foreach { hostname =>
      hostname.value should be("123.abc.foo-bar")
    }
  }

}

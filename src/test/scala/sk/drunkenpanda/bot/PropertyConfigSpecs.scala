package sk.drunkenpanda.bot

import org.specs2.mutable._

/**
 * @author Jan Ferko
 */
class PropertyConfigSpecs extends Specification {

  "PropertyConfig" should {

    "load configuration from property file" in {
      val config = Config.fromProperties("test.properties")
      config.host must beEqualTo("irc.freenode.net")
      config.port must beEqualTo(6667)
      config.channel must beEqualTo("#drunkenpanda")
      config.nickname must beEqualTo("DrunkenPanda")
      config.realname must beEqualTo("Drunken Panda")
    }

  }

}

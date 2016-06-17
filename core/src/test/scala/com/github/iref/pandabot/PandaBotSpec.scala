package com.github.iref.pandabot

import com.github.iref.pandabot.protocol.Message
import org.scalacheck.Arbitrary
import org.scalatest.{ FlatSpec, Matchers }
import org.scalatest.prop.{ Configuration, GeneratorDrivenPropertyChecks }

trait PropertyCheckerSettings extends Configuration {

  lazy val checkConfiguration: PropertyCheckConfig =
    PropertyCheckConfig(
      minSuccessful = 200,
      minSize = 0,
      maxSize = 20
    )

}

/**
 * Opinionated specification trait stack to improve consistency and
 * provide boilerplate in tests.
 */
abstract class PandaBotSpec(component: String) extends FlatSpec
    with Matchers
    with GeneratorDrivenPropertyChecks
    with PropertyCheckerSettings
    with Generators {

  implicit override val generatorDrivenConfig = checkConfiguration

  implicit val messageArbitrary: Arbitrary[Message] = Arbitrary(message)

  behavior of component

}

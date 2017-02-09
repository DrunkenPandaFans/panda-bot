package me.janferko.pandabot

import org.scalatest.prop.{Configuration, GeneratorDrivenPropertyChecks}
import org.scalatest.{FlatSpec, Matchers}

trait PropertyCheckerSettings extends Configuration {

  lazy val checkConfiguration: PropertyCheckConfiguration =
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
    with PropertyCheckerSettings {

  implicit override val generatorDrivenConfig: PropertyCheckConfiguration = checkConfiguration

  behavior of component

}

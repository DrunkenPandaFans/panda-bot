package me.janferko.pandabot.protocol

import me.janferko.pandabot.PandaBotSpec
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.EitherValues

final class ParameterSpec extends PandaBotSpec with EitherValues {

  val nonEmptyString: Gen[String] =
    for {
      n <- Gen.posNum[Int]
      chars <- Gen.listOfN(n, Gen.alphaNumChar)
    } yield chars.mkString("")

  val nicknameGen: Gen[String] = {
    val part = Gen.oneOf(nonEmptyString, Gen.oneOf(Parameter.SpecialCharacters))
    for {
      n <- Gen.posNum[Int]
      parts <- Gen.listOfN(n, part)
    } yield parts.mkString("")
  }

  val usernameGen: Gen[String] = Arbitrary.arbitrary[String]
    .filter(s => !s.isEmpty && s.forall(!_.isWhitespace))

  val hostnameGen: Gen[String] = {
    val label = Gen.frequency((80, Gen.alphaNumChar), (20, Gen.const('-')))
    val labelGen = for {
      prefix <- Gen.alphaNumChar
      suffix <- Gen.oneOf(Gen.alphaNumChar, Gen.const(""))
      n <- Gen.choose(0, 61)
      parts <- Gen.listOfN(n, label)
    } yield prefix + parts.mkString("") + suffix

    for {
      n <- Gen.choose(1, 3)
      labels <- Gen.listOfN(n, labelGen)
    } yield labels.mkString(".")
  }

  "Nickname" should "reject empty values" in {
    Seq("", "   ", "\t\n").foreach { s =>
      val result = Nickname.validate(s)
      withClue(s"Nickname [$s]: ") {
        result.left.value shouldBe a[EmptyParameterValue]
      }
    }
  }

  it should "reject values with unexpected characters" in {
    Seq("koala@vienna", "koala bear", "koala\teating").foreach { s =>
      val result = Nickname.validate(s)
      withClue(s"Nickname [$s]: ") {
        result.left.value shouldBe a[UnexpectedCharacter]
      }
    }
  }

  it should "accept correct values" in {
    forAll(nicknameGen) { nick =>
      val result = Nickname.validate(nick)
      result.right.value.get should be(nick)
    }
  }

  "Username" should "reject empty values" in {
    Seq("", "   ", "\t\n").foreach { s =>
      val result = Username.validate(s)
      withClue(s"Username [$s]: ") {
        result.left.value shouldBe a[EmptyParameterValue]
      }
    }
  }

  it should "reject values with whitespace characters" in {
    Seq("Koala Jenny", "Koala\tTim").foreach { s =>
      val result = Username.validate(s)
      withClue(s"Nickname [$s]: ") {
        result.left.value shouldBe a[UnexpectedWhitespaceCharacters]
      }
    }
  }

  it should "accept correct values" in {
    forAll(usernameGen) { user =>
      val result = Username.validate(user)
      result.right.value.get should be(user)
    }
  }

  "Hostname" should "reject empty values" in {
    Seq("", "   ", "\t\n").foreach { s =>
      val result = Hostname.validate(s)
      withClue(s"Hostname [$s]: ") {
        result should be('left)
        result.left.value shouldBe a[EmptyParameterValue]
      }
    }
  }

  it should "reject hostname longer than 255 characters" in {
    val result = Hostname.validate("a" * 256)
    result should be('left)
    result.left.value shouldBe a[HostnameTooLong]
  }

  it should "reject hostname if it ends with dot" in {
    val result = Hostname.validate("koala.bear.")
    result should be('left)
    result.left.value shouldBe a[HostnameEndsWithDot]
  }

  it should "reject hostname with invalid label" in {
    Seq("-koala.bear", "koala-.bear", "a" * 64, "Koala.kate@bamboo.tree^cn").foreach { s =>
      val result = Hostname.validate(s)
      withClue(s"Hostname [$s]: ") {
        result should be('left)
        result.left.value shouldBe a[WrongHostnameLabel]
      }
    }
  }

  it should "accept correct hostname" in {
    forAll(hostnameGen) { host =>
      val result = Hostname.validate(host)
      result should be('right)
      result.right.value.get should be(host)
    }
  }
}

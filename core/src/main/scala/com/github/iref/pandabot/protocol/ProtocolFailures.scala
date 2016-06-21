package com.github.iref.pandabot.protocol

import cats.data.Xor

/**
 * Indicates a failure to handle IRC [[Message]].
 */
sealed abstract class ProtocolFailure extends RuntimeException {

  /**
   * Provides a message describing failure.
   */
  def message: String

  /**
   * Overridden for sensible failure logging.
   */
  final override def getMessage: String = message

}

/**
 * Indicates error while decoding an IRC [[Message]].
 *
 * @param reason Reason of failure. May safely be shown to client.
 *               Should not contain any part of message.
 * @param detail Contains any relevant details of failure omitted from
 *               reason.
 */
final case class DecodingFailure(reason: String = "", detail: String = "") extends ProtocolFailure {
  override def message: String =
    if (reason.isEmpty) detail
    else if (detail.isEmpty) reason
    else s"$reason. $detail"
}

object DecodingResult {

  /**
   *
   */
  def success[A](a: => A): DecodingResult[A] = Xor.Right(a)

  /**
   *
   */
  def fail[A](failure: DecodingFailure): DecodingResult[A] = Xor.Left(failure)
}


package com.github.iref.pandabot

import cats.data.Xor

package object protocol {

  /**
   * Result of decoding A from IRC protocol or failure if decoder wasn't able to
   * decode message.
   */
  type DecodingResult[+A] = Xor[DecodingFailure, A]

}

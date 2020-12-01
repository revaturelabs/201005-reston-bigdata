package runner

import purple.Q1.HashtagsByRegion
import purple.Q2.HashtagsWithCovid

object Runner {
  def main(args: Array[String]): Unit = {
    // Command Line Interface format that accepts params
    // Have a use case for your question below
    args match {
      case Array("hbr") => HashtagsByRegion.getHashtagsByRegion()
      case Array("hwb") => HashtagsWithCovid.getHashtagsByRegion()
      case _ => println("Hello World")
    }
  }
}

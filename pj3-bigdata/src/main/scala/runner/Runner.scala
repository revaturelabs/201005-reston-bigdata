package runner

import purple.Q1.HashtagsByRegion

object Runner {
  def main(args: Array[String]): Unit = {
    // Command Line Interface format that accepts params
    // Have a use case for your question below
    args match {
      case Array("hbr") => HashtagsByRegion.getHashtagsByRegion
      case _ => println("Hello World")
    }
  }
}

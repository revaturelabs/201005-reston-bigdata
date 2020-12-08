package purple

import main.scala.purple.Q1.HashtagsByRegion
import purple.Q2.HashtagsWithCovid

object Runner {
  def main(args: Array[String]): Unit = {
    // Command Line Interface format that accepts params
    // Have a use case for your question below
    args match {
      //purple team's questions
      case Array("hbr") => HashtagsByRegion.getHashtagsByRegion()
      case Array("hbr", region) => HashtagsByRegion.getHashtagsByRegion(region)
      case Array("hwc") => HashtagsWithCovid.getHashtagsWithCovid()
      case _ => println("Hello World")
    }
  }
}
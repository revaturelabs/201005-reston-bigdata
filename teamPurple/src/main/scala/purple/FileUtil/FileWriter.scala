package main.scala.purple.FileUtil

object FileWriter {

  def writeDataFrameToFile(dataFrame: DataFrame, outputFilename: String) = {
    dataFrame
      .limit(100)
      .write
      .format("csv")
      .save(s"$outputFilename")
  }
}

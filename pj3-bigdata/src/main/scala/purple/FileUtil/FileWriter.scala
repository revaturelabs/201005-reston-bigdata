package purple.FileUtil

import org.apache.spark.sql.DataFrame

object FileWriter {

  def writeDataFrameToFile(dataFrame: DataFrame, outputFilename: String) = {
      dataFrame
        .limit(100)
        .write
        .format("csv")
        .save(s"$outputFilename")
  }
}

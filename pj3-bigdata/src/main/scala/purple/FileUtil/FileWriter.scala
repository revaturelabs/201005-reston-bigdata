package purple.FileUtil

import org.apache.spark.sql.DataFrame

object FileWriter {

  def writeDataFrameToFile(dataFrame: DataFrame, outputFilename: String, maxRecords: Int = 100) = {
      dataFrame
        .limit(maxRecords)
        .write
        .format("csv")
        .save(s"s3a://adam-king-848/results/purple/$outputFilename")
  }
}

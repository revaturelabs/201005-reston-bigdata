package runner

import org.apache.spark.sql.SparkSession
import purple.Q1.HashtagsByRegion
import purple.Q2.HashtagsWithCovid

object Runner {
  def main(args: Array[String]): Unit = {

    val appName = "hashtagRegions"
    val spark = SparkSession.builder()
      .appName(appName)
      .master("local[8]")
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN")

    // Command Line Interface format that accepts params
    // Have a use case for your question below
    args match {
        //purple team's questions
      case Array("hbr") => HashtagsByRegion.getHashtagsByRegion(spark)
      case Array("hbr", region) => HashtagsByRegion.getHashtagsByRegion(spark, region)
      case Array("hwc") => HashtagsWithCovid.getHashtagsWithCovid(spark)
      case _ => println("Hello World")
    }
  }
}

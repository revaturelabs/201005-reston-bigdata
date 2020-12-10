package purple.Q1

import org.apache.spark.sql.{DataFrame, SparkSession, functions}
import purple.FileUtil.FileWriter.writeDataFrameToFile
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

object HashtagsByRegion {

  def getHashtagsByRegion(spark: SparkSession, region: String = null): Unit = {
    //What are the hashtags used to describe COVID-19 by Region (e.g. #covid, #COVID-19, #Coronavirus, #NovelCoronavirus)?
    val staticDf = spark.read.json("s3a://adam-king-848/data/twitter_data.json")
    question1(spark, staticDf, region)
  }

  def getHashtagsByRegionAll(spark: SparkSession): Unit = {
    //What are the hashtags used to describe COVID-19 by Region (e.g. #covid, #COVID-19, #Coronavirus, #NovelCoronavirus)?
    val staticDf = spark.read.json("s3a://adam-king-848/data/twitter_data.json")
    question1all(spark, staticDf)
  }

  private def question1(spark: SparkSession, df: DataFrame, region: String): Unit = {
    import spark.implicits._
    val startTime = System.currentTimeMillis()
    var outputFilename: String = null

    val newdf = df
      .filter(!functions.isnull($"place"))
      .select($"entities.hashtags.text", $"place.country")
      //map to Row(List(Hashtags),Region)
      .map(tweet => {
        (tweet.getList[String](0).toList.map(_.toLowerCase()), RegionDictionary.reverseMapSearch(tweet.getString(1)))
      })
      .withColumnRenamed("_1", "Hashtags")
      .withColumnRenamed("_2", "Region")

    // If we are passed a region, we need to filter by it
    // Otherwise we present all of the information
    if (region != null) {
      val sorteddf = newdf
        .filter($"region" === region)
        //explode List(Hashtags),Region to own rows resulting in Row(Hashtag,Region)
        .select(functions.explode($"Hashtags").as("Hashtag"), $"Region")
        //group by the same Region and Hashtag
        .groupBy("Region", "Hashtag")
        //count total of each Region/Hashtag appearance
        .count()
        .orderBy(functions.desc("Count"))

      outputFilename = s"hbr-${region.replaceAll("\\s+","")}-$startTime"
      writeDataFrameToFile(sorteddf, outputFilename)
    } else {
      val sorteddf = newdf
        //explode List(Hashtags),Region to own rows resulting in Row(Hashtag,Region)
        .select(functions.explode($"Hashtags").as("Hashtag"), $"Region")
        //group by the same Region and Hashtag
        .groupBy("Region", "Hashtag")
        //count total of each Region/Hashtag appearance
        .count()
        .orderBy(functions.desc("Count"))

      outputFilename = s"hbr-AllRegions-$startTime"
      writeDataFrameToFile(sorteddf, outputFilename)
    }
  }

  private def question1all(spark: SparkSession, df: DataFrame): Unit = {
    import spark.implicits._
    val startTime = System.currentTimeMillis()
    var outputFilename: String = null

    val newdf = df
      .filter(!functions.isnull($"place"))
      .select($"entities.hashtags.text", $"place.country")
      //map to Row(List(Hashtags),Region)
      .map(tweet => {
        (tweet.getList[String](0).toList.map(_.toLowerCase()), RegionDictionary.reverseMapSearch(tweet.getString(1)))
      })
      .withColumnRenamed("_1", "Hashtags")
      .withColumnRenamed("_2", "Region")
      //explode List(Hashtags),Region to own rows resulting in Row(Hashtag,Region)
      .select(functions.explode($"Hashtags").as("Hashtag"), $"Region")
      //group by the same Region and Hashtag
      .groupBy("Region", "Hashtag")
      //count total of each Region/Hashtag appearance
      .count()

    val greatdf = newdf
      .orderBy(functions.desc("Count"))

    outputFilename = s"hbr-all-$startTime"
    writeDataFrameToFile(greatdf, outputFilename)

    RegionDictionary.getRegionList.foreach(region => {
      val bestdf = newdf
        .filter($"Region" === region)
        .orderBy(functions.desc("Count"))

      outputFilename = s"hbr-${region.replaceAll("\\s+","")}-$startTime"
      writeDataFrameToFile(bestdf, outputFilename)
    })
  }
}

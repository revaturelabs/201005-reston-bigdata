package purple.Q1

import org.apache.spark.sql.{DataFrame, SparkSession, functions}
import purple.FileUtil.FileWriter.writeDataFrameToFile

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

object HashtagsByRegion {

  def getHashtagsByRegion(region: String = null): Unit = {
    //What are the hashtags used to describe COVID-19 by Region (e.g. #covid, #COVID-19, #Coronavirus, #NovelCoronavirus)?

    //for local testing purposes (pass spark session from runner in prod)
    val appName = "hashtagRegions"
    val spark = SparkSession.builder()
      .appName(appName)
      .master("local[8]")
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN")

    val staticDf = spark.read.json("src/main/scala/purple/exampleTwitterData.jsonl")

    println("QUESTION 1 (With Sample Data)")
    question1(spark, staticDf, region)
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
        (tweet.getList[String](0).toList, RegionDictionary.reverseMapSearch(tweet.getString(1)))
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

        sorteddf.show(100)

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

        sorteddf.show(100)

      outputFilename = s"hbr-AllRegions-$startTime"
      writeDataFrameToFile(sorteddf, outputFilename)
    }
  }
}

package purple.Q1

import org.apache.spark.sql.{DataFrame, SparkSession, functions}
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
    val newdf = df
      .filter(!functions.isnull($"place"))
      .select($"full_text", $"place.country")
      //map to Row(List(Hashtags),Country)
      .map(tweet => {
        (getHashtags(tweet.getString(0)), tweet.getString(1))
      })
      //map to Row(List(Hashtags),Region)
      .map(tweet => {
        (tweet._1, RegionDictionary.reverseMapSearch(tweet._2))
      })
      .withColumnRenamed("_1", "Hashtags")
      .withColumnRenamed("_2", "Region")


    // If we are passed a region, we need to filter by it
    // Otherwise we present all of the information
    if (region != null) {
      newdf
        .filter($"region" === region)
        //explode List(Hashtags),Region to own rows resulting in Row(Hashtag,Region)
        .select(functions.explode($"Hashtags").as("Hashtag"), $"Region")
        //group by the same Region and Hashtag
        .groupBy("Region", "Hashtag")
        //count total of each Region/Hashtag appearance
        .count()
        .orderBy(functions.desc("Count"))
        .show()
    } else {
      newdf
        //explode List(Hashtags),Region to own rows resulting in Row(Hashtag,Region)
        .select(functions.explode($"Hashtags").as("Hashtag"), $"Region")
        //group by the same Region and Hashtag
        .groupBy("Region", "Hashtag")
        //count total of each Region/Hashtag appearance
        .count()
        .orderBy(functions.desc("Count"))
        .show()
    }
  }

  private def getHashtags(text: String): List[String] = {
    val re = """(#\S+)""".r
    re.findAllIn(text).toList
  }
}

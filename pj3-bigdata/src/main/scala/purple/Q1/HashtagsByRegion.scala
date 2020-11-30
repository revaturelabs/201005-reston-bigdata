package purple.Q1

import org.apache.spark.sql.{DataFrame, SparkSession, functions}
import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

object HashtagsByRegion {

  def getHashtagsByRegion {
    //What are the hashtags used to describe COVID-19 by Region (e.g. #covid, #COVID-19, #Coronavirus, #NovelCoronavirus)?

    val appName = "hashtagRegions"

    val spark = SparkSession.builder()
      .appName(appName)
      .master("local[8]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("WARN")

    val staticDf = spark.read.format("csv")
      .option("header", "true")
      .option("delimiter", "\t")
      .load("src/main/scala/purple/Q1/temp.tsv")

    println("QUESTION 1")
    question1(spark, staticDf)
  }

  private def question1(spark: SparkSession, df: DataFrame): Unit = {
    import spark.implicits._
    df
      .select($"Text", $"Country")
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
      //explode List(Hashtags),Region to own rows resulting in Row(Hashtag,Region)
      .select(functions.explode($"Hashtags").as("Hashtag"), $"Region")
      //group by the same Region and Hashtag
      .groupBy("Region", "Hashtag")
      //count total of each Region/Hashtag appearance
      .count()
      .orderBy(functions.desc("Region"))
      .show()
  }

  private def getHashtags(text: String): List[String] = {
    val re = """(#\S+)""".r
    re.findAllIn(text).toList
  }
}

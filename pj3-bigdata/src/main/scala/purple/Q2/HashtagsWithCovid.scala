package purple.Q2

import org.apache.spark.sql.{DataFrame, SparkSession, functions}

import scala.collection.convert.ImplicitConversions.`collection AsScalaIterable`

object HashtagsWithCovid {

  def getHashtagsWithCovid(): Unit = {
    //What are the top 10 commonly used hashtags used alongside COVID hashtags?

    //for local testing purposes (pass spark session from runner in prod)
    val appName = "hashtagWithCovid"
    val spark = SparkSession.builder()
      .appName(appName)
      .master("local[8]")
      .getOrCreate()
    spark.sparkContext.setLogLevel("WARN")

    val sampleStaticDf = spark.read.json("src/main/scala/purple/exampleTwitterData.jsonl")
    println("QUESTION 2 (with Sample Data)")
    question2(spark, sampleStaticDf)
  }

  private def question2(spark: SparkSession, df: DataFrame): Unit = {
    import spark.implicits._
    val covidRelatedWordsList = CovidTermsList.getTermsList
    df
      .select($"entities.hashtags.text")
      //map to Row(List(Hashtags))
      .map(tweet => {
        tweet.getList[String](0).toList
      })
      .withColumnRenamed("value", "Hashtags")
      //filter to only lists with greater than 1 hashtags (2+)
      .filter(functions.size($"Hashtags").gt(1))
      //filter to only lists that contain a word from our filter list
      .filter(hashtags => {
        val hashtagList = hashtags.getList[String](0)
        //this filter statement seems inefficient
        hashtagList.exists(
          hashtag => covidRelatedWordsList.exists(
            covidHashtag => hashtag.toLowerCase().contains(covidHashtag.toLowerCase())
          )
        )
      })
      //explode out all remaining List(Hashtags)
      .select(functions.explode($"Hashtags").as("Hashtag"))
      //remove all items that are on the our filter list
      .filter(hashtag => {
        val hashtagStr = hashtag.getString(0)
        !covidRelatedWordsList.exists(
          covidHashtag => hashtagStr.toLowerCase().contains(covidHashtag.toLowerCase())
        )
      })
      .groupBy($"Hashtag")
      .count()
      .orderBy(functions.desc("Count"))
      .show(10)
  }
}

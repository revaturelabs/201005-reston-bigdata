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

    val sampleStaticDf = spark.read.parquet("src/main/scala/purple/Q2/wednesday.parquet")
    println("QUESTION 2 (with Sample Data)")
    question2(spark, sampleStaticDf)
  }

  private def question2(spark: SparkSession, df: DataFrame): Unit = {
    import spark.implicits._
    val covidRelatedWordsList = List("corona","Corona","Covid","COVID","covid")
    val covidHashtags = covidRelatedWordsList.map(word => s"$word")
    df
      .select($"data.text")
      //map to Row(List(Hashtags))
      .map(tweet => {
        getHashtags(tweet.getString(0))
      })
      .withColumnRenamed("value", "Hashtags")
      //filter to only lists with greater than 1 hashtags (2+)
      .filter(functions.size($"Hashtags").gt(1))
      //filter to only lists that contain a word from our filter list
      .filter(hashtags => {
        val hashtagList = hashtags.getList[String](0)
        //this filter statement seems inefficient
        hashtagList.exists(hashtag => covidHashtags.exists(hashtag.contains(_)))
      })
      //explode out all remaining List(Hashtags)
      .select(functions.explode($"Hashtags").as("Hashtag"))
      //remove all items that are on the our filter list
      .filter(hashtag => {
        val hashtagStr = hashtag.getString(0)
        !covidHashtags.exists(hashtagStr.contains(_))
      })
      .groupBy($"Hashtag")
      .count()
      .orderBy(functions.desc("Count"))
      .show(10)
  }

  private def getHashtags(text: String): List[String] = {
    val re = """(#\S+)""".r
    re.findAllIn(text).toList
  }

}

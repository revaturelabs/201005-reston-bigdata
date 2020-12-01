package green.Q4

import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.apache.spark.sql.functions.{count, desc}

object Question4 {
  def GetTweetsByCount(spark : SparkSession, path : String) : Unit = {
    import spark.implicits._

    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv(path)

    val tweetCountsByDay = df.select($"date")
      .groupBy($"date")
      .agg(
        count($"date") as "num_tweets"
      )
      .orderBy($"date")

    tweetCountsByDay.show(1000, false)
  }

  def GetTweetsByCountDesc(spark : SparkSession, path : String) : Unit = {
    import spark.implicits._

    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv(path)

    val tweetCountsByDayDesc = df.select($"date")
      .groupBy($"date")
      .agg(
        count($"date") as "num_tweets"
      )
      .orderBy(desc("num_tweets"))

    tweetCountsByDayDesc.show(1000, false)
  }
}

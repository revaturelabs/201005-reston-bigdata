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
  
  /**
   * Shows How many times a day there are infections ordered by date
   * @param spark the Spark Session
   * @param path the path of the TSV file
   */
  def getDataByDate(spark: SparkSession, path: String): Unit = {
    import spark.implicits._

    //reads in the tsv
    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv(path)

    //filters to only get ages 0 - 29, then groups by days and orders by date
    val dataSpikes = df
      .select($"cdc_report_dt", $"age_group")
      .filter($"age_group" === "0 - 9 Years" || $"age_group" === "10 - 19 Years" || $"age_group" === "20 - 29 Years")
      .groupBy("cdc_report_dt")
      .count()
      .withColumnRenamed("cdc_report_dt", "Date")
      .orderBy(desc("Date"))

    dataSpikes.show()
  }

  /**
   * Shows How many times a day there are infections ordered by infection count
   * @param spark the Spark Session
   * @param path the path of the TSV file
   */
  def getDataByCount(spark: SparkSession, path: String): Unit = {
    import spark.implicits._

    //reads in the tsv
    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv(path)

    //filters to only get ages 0 - 29, then groups by days and orders by amount of infections
    val dataSpikes = df
      .select($"cdc_report_dt", $"age_group")
      .filter($"age_group" === "0 - 9 Years" || $"age_group" === "10 - 19 Years" || $"age_group" === "20 - 29 Years")
      .groupBy("cdc_report_dt")
      .count()
      .withColumnRenamed("cdc_report_dt", "Date")
      .orderBy(desc("Count"))

    dataSpikes.show()
  }
}

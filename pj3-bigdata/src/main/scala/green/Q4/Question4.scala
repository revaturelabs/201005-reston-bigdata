package green.Q4

import org.apache.spark.sql.{Dataset, Row, SparkSession}
import org.apache.spark.sql.functions.{avg, count, desc}

object Question4 {

  def GetTweetsByCount(spark : SparkSession) : Unit = {
    import spark.implicits._

    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv("s3a://adam-king-848/data/q4_a_full.tsv")

    val tweetCountsByDay = df.select($"date")
      .groupBy($"date")
      .agg(
        count($"date") as "num_tweets"
      )
      .orderBy($"date")

    tweetCountsByDay.show()
  }

  def GetTweetsByCountDesc(spark : SparkSession) : Unit = {
    import spark.implicits._

    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv("s3a://adam-king-848/data/q4_a_full.tsv")

    val tweetCountsByDayDesc = df.select($"date")
      .groupBy($"date")
      .agg(
        count($"date") as "num_tweets"
      )
      .orderBy(desc("num_tweets"))

    tweetCountsByDayDesc.show()
  }
  
  /**
   * Shows How many times a day there are infections ordered by date
   * @param spark the Spark Session
   */
  def GetCasesByDate(spark: SparkSession): Unit = {
    import spark.implicits._

    //reads in the tsv
    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv("s3a://adam-king-848/data/CDC_Covid_archive.tsv")

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
   */
  def GetCasesByCount(spark: SparkSession): Unit = {
    import spark.implicits._

    //reads in the tsv
    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv("s3a://adam-king-848/data/CDC_Covid_archive.tsv")

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
  
  /**
   * Shows How many times a day there are infections ordered by infection count in ages 0-9
   * @param spark the Spark Session
   */
  def GetCasesByCount09(spark: SparkSession): Unit = {
    import spark.implicits._

    //reads in the tsv
    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv("s3a://adam-king-848/data/CDC_Covid_archive.tsv")

    //filters to only get ages 0 - 9, then groups by days and orders by amount of infections
    val dataSpikes = df
      .select($"cdc_report_dt", $"age_group")
      .filter($"age_group" === "0 - 9 Years")
      .groupBy("cdc_report_dt")
      .count()
      .withColumnRenamed("cdc_report_dt", "Date")
      .orderBy(desc("Count"))

    dataSpikes.show()
  }
  
  /**
   * Shows How many times a day there are infections ordered by infection count in ages 10-19
   * @param spark the Spark Session
   */
  def GetCasesByCount1019(spark: SparkSession): Unit = {
    import spark.implicits._

    //reads in the tsv
    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv("s3a://adam-king-848/data/CDC_Covid_archive.tsv")

    //filters to only get ages 10 - 19, then groups by days and orders by amount of infections
    val dataSpikes = df
      .select($"cdc_report_dt", $"age_group")
      .filter($"age_group" === "10 - 19 Years")
      .groupBy("cdc_report_dt")
      .count()
      .withColumnRenamed("cdc_report_dt", "Date")
      .orderBy(desc("Count"))

    dataSpikes.show()
  }
  
  /**
   * Shows How many times a day there are infections ordered by infection count in ages 20-29
   * @param spark the Spark Session
   */
  def GetCasesByCount2029(spark: SparkSession): Unit = {
    import spark.implicits._

    //reads in the tsv
    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv("s3a://adam-king-848/data/CDC_Covid_archive.tsv")

    //filters to only get ages 20 - 29, then groups by days and orders by amount of infections
    val dataSpikes = df
      .select($"cdc_report_dt", $"age_group")
      .filter($"age_group" === "20 - 29 Years")
      .groupBy("cdc_report_dt")
      .count()
      .withColumnRenamed("cdc_report_dt", "Date")
      .orderBy(desc("Count"))

    dataSpikes.show()
  }
  
    /**
   * Shows How many times a day there are infections ordered by infection count in ages 30-39
   * @param spark the Spark Session
   */
  def GetCasesByCount3039(spark: SparkSession): Unit = {
    import spark.implicits._

    //reads in the tsv
    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv("s3a://adam-king-848/data/CDC_Covid_archive.tsv")

    //filters to only get ages 30 - 39, then groups by days and orders by amount of infections
    val dataSpikes = df
      .select($"cdc_report_dt", $"age_group")
      .filter($"age_group" === "30 - 39 Years")
      .groupBy("cdc_report_dt")
      .count()
      .withColumnRenamed("cdc_report_dt", "Date")
      .orderBy(desc("Count"))

    dataSpikes.show()
  }
  
  def GetPeakTweetCount(spark : SparkSession, path : String) : Long = {
    import spark.implicits._

    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv(path)

    // Find peak
    val peak = df.select($"date")
      .groupBy($"date")
      .agg(
        count($"date") as "num_tweets"
      )
      .orderBy(desc("num_tweets"))

    peak.first().getLong(1)
  }

  def GetCurrentDayTweetCount(spark : SparkSession, path : String) : Long = {
    import spark.implicits._

    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv(path)

    val currentTweetsForDay = df.select($"date")
      .groupBy($"date")
      .agg(
        count($"date") as "num_tweets"
      )
      .orderBy(desc("date"))
      .take(2)

    currentTweetsForDay.last(1).toString.toLong
  }

  def GetAVGTweetCountForMonth(spark : SparkSession, path : String) : Int = {
    import spark.implicits._

    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv(path)

    val avgTweetsForWeek = df.select($"date")
      .groupBy($"date")
      .agg(
        count($"date") as "num_tweets"
      )
      .orderBy(desc("date"))
      .take(32)
      .drop(2)

    var avg : Long = 0
    avgTweetsForWeek.foreach( row =>
      avg += row.getLong(1)
    )

    avg.toInt /30
  }

  def GetAVGTweetCountForWeek(spark : SparkSession, path : String) : Int = {
    import spark.implicits._

    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv(path)

    val avgTweetsForWeek = df.select($"date")
      .groupBy($"date")
      .agg(
        count($"date") as "num_tweets"
      )
      .orderBy(desc("date"))
      .take(9)
      .drop(2)

    var avg : Long = 0
    avgTweetsForWeek.foreach( row =>
      avg += row.getLong(1)
    )

    avg.toInt / 7
  }

  def GetYesterdayTweetCount(spark : SparkSession, path : String) : Int = {
    import spark.implicits._

    val df = spark.read
      .option("header", "true")
      .option("delimiter", "\t")
      .csv(path)

    val avgTweetsForWeek = df.select($"date")
      .groupBy($"date")
      .agg(
        count($"date") as "num_tweets"
      )
      .orderBy(desc("date"))
      .take(3)
      .drop(2)

    var avg : Long = 0
    avgTweetsForWeek.foreach( row =>
      avg += row.getLong(1)
    )

    avg.toInt
  }

  def GetTrendingPercentageSincePeak(spark : SparkSession, path : String) : String = {
    val peak = GetPeakTweetCount(spark, path).toDouble
    val currentTweetCount = GetCurrentDayTweetCount(spark, path).toDouble

    var trendingPercentage : String = s"up ${(((peak - currentTweetCount).abs / peak) * 100).round}%"
    if (peak > currentTweetCount) {
      trendingPercentage = s"down ${(((peak - currentTweetCount) / peak) * 100).round}%"
    }

    trendingPercentage
  }

  def GetTrendingPercentageOverMonth(spark : SparkSession, path : String) : String = {
    val avgTweetCountLastMonth = GetAVGTweetCountForMonth(spark, path).toDouble
    val currentTweetCount = GetCurrentDayTweetCount(spark, path).toDouble

    var trendingPercentage : String = s"up ${(((avgTweetCountLastMonth - currentTweetCount).abs / avgTweetCountLastMonth) * 100).round}%"
    if (avgTweetCountLastMonth > currentTweetCount) {
      trendingPercentage = s"down ${(((avgTweetCountLastMonth - currentTweetCount) / avgTweetCountLastMonth) * 100).round}%"
    }

    trendingPercentage
  }

  def GetTrendingPercentageOverWeek(spark : SparkSession, path : String) : String = {
    val avgTweetCountLastWeek = GetAVGTweetCountForWeek(spark, path).toDouble
    val currentTweetCount = GetCurrentDayTweetCount(spark, path).toDouble

    var trendingPercentage : String = s"up ${(((avgTweetCountLastWeek - currentTweetCount).abs / avgTweetCountLastWeek) * 100).round}%"
    if (avgTweetCountLastWeek > currentTweetCount) {
      trendingPercentage = s"down ${(((avgTweetCountLastWeek - currentTweetCount) / avgTweetCountLastWeek) * 100).round}%"
    }

    trendingPercentage
  }

  def GetTrendingPercentageSinceYesterday(spark : SparkSession, path : String) : String = {
    val avgTweetCountSinceYesterday = GetYesterdayTweetCount(spark, path).toDouble
    val currentTweetCount = GetCurrentDayTweetCount(spark, path).toDouble

    var trendingPercentage : String = s"up ${(((avgTweetCountSinceYesterday - currentTweetCount).abs / avgTweetCountSinceYesterday) * 100).round}%"
    if (avgTweetCountSinceYesterday > currentTweetCount) {
      trendingPercentage = s"down ${(((avgTweetCountSinceYesterday - currentTweetCount) / avgTweetCountSinceYesterday) * 100).round}%"
    }

    trendingPercentage
  }

  def PrintTrendingStatus(spark: SparkSession): Unit = {
    val path = "s3a://adam-king-848/data/q4_a_full.tsv"

    println(s"Trend of discussion is ${GetTrendingPercentageSincePeak(spark, path)} since peak")
    println(s"Compared to last month, trend of discussion is ${GetTrendingPercentageOverMonth(spark, path)}")
    println(s"Compared to last week, trend of discussion is ${GetTrendingPercentageOverWeek(spark, path)}")
    println(s"Compared to yesterday, trend of discussion is ${GetTrendingPercentageSinceYesterday(spark, path)}")
  }

  def PrintQuestion4(spark : SparkSession) : Unit = {
    GetTweetsByCount(spark)
    PrintTrendingStatus(spark)
    GetCasesByCount(spark)
  }

}

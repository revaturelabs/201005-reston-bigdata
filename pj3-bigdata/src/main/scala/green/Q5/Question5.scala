package green.Q5

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, count, desc, substring}

object Question5 {

  /**
   * Program breaks down large COVID-19 related tweets into partitioned parquets. Perform three different
   * queries to answer the question (5) of when was COVID-19 being discussed the most by months, days, and hours
   * of the day that has the highest discussion count.
   *
   * @param spark The SparkSession for SparkSQL
   * @param path The path of the full_dataset.tsv file
   */
  def getMostDiscussion(spark: SparkSession, path: String)= {

    // Reading tsv and turning it into a DataFrame
    val df = spark.read.option("header", true).option("sep", "\t").csv(path)

    // Partition parquet into multiple files based on dates
    df.write.partitionBy("date").parquet("tweet-partitioned.parquet")
    val readDf = spark.read.parquet("tweet-partitioned.parquet")

    // Create new view for original partitioned files to use pure SQL
    val viewDf = readDf.createOrReplaceTempView("viewDf")

    // Finding count of COVID-19 related tweets per month
    val month = readDf.groupBy(substring(col("date"), 6, 2).as("months"))
      .agg(count("*").as("tweets"))
    month.orderBy(desc("tweets")).show()

    // Save result into ONE csv file so that we can graph
    // (using coalesce to merge all parts when the cores are finished --
    // the parts are created based on data locality in HDFS)
    //month.coalesce(1).write.format("com.databricks.spark.csv").save("months_result.csv")

    // Finding count of COVID-19 related tweets per day
    val day = readDf.groupBy(col("date").as("days")).agg(count("*").as("tweets"))
    day.orderBy(desc("tweets")).show()
    //day.coalesce(1).write.format("com.databricks.spark.csv").save("days_result.csv")

    // Finding the day that has highest COVID-19 related tweets
    val maxDay = readDf.groupBy(col("date").as("days")).count()
    maxDay.orderBy(desc("count")).limit(1).createOrReplaceTempView("viewMax")

    // Finding count of COVID-19 related tweets per hour of the day that has the highest tweets count
    val hour = spark.sql("SELECT viewDf.date, SUBSTRING(viewDf.time, 1, 2) AS hour, COUNT(*) AS tweets " +
      "FROM viewMax, viewDf " +
      "WHERE viewDf.date = viewMax.days " +
      "GROUP BY viewDf.date, hour " +
      "ORDER BY tweets DESC").show()
    //hour.coalesce(1).write.format("com.databricks.spark.csv").save("hours_result.csv")

  }
}

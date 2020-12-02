package blue

import java.text.SimpleDateFormat

import breeze.linalg.{DenseVector, linspace}
import breeze.plot.{Figure, plot}
import org.apache.spark.sql.{DataFrame, SparkSession, functions}
import java.util.Date

import scala.collection.mutable.ArrayBuffer
import java.util.Date

import breeze.numerics.round

object RankRegions {
  def rankByMetric(spark: SparkSession, fullDS: DataFrame, metric: String, op: String = "avg"): DataFrame ={
    import spark.implicits._
    var oneTimeMetric: DataFrame = spark.emptyDataFrame
    op match {
      case "avg" => {
        oneTimeMetric = fullDS
          .groupBy("region")
          .agg(functions.avg(s"$metric") as s"$metric")
          .sort(functions.col(s"$metric") desc)
      }
      case "latest" => {
        val latestDate = fullDS.select(functions.max("date")).collect().map(_.getString(0))
        oneTimeMetric = fullDS
          .select("region", metric)
          .where($"date" === latestDate(0))
          .sort(functions.col(s"$metric") desc)
      }
      case "max" => {
        oneTimeMetric = fullDS
          .groupBy("region")
          .agg(functions.max(s"$metric") as s"$metric")
          .sort(functions.col(s"$metric") desc)
      }
      case _ => {
        oneTimeMetric = fullDS
          .groupBy("region")
          .agg(functions.avg(s"$metric") as s"$metric")
          .sort(functions.col(s"$metric") desc)
      }
    }
    oneTimeMetric
  }

  def rankByMetricLow(spark: SparkSession, fullDS: DataFrame, metric: String, op: String = "avg"): DataFrame ={
    import spark.implicits._
    var oneTimeMetric: DataFrame = spark.emptyDataFrame
    op match {
      case "avg" => {
        oneTimeMetric = fullDS
          .groupBy("region")
          .agg(functions.round(functions.avg(s"$metric"), 2) as s"$metric")
          .sort(functions.col(s"$metric"))
      }
      case "latest" => {
        val latestDate = fullDS.select(functions.max("date")).collect().map(_.getString(0))
        oneTimeMetric = fullDS
          .select("region", metric)
          .where($"date" === latestDate(0))
          .sort(functions.round(functions.col(s"$metric"), 2))
      }
      case "max" => {
        oneTimeMetric = fullDS
          .groupBy("region")
          .agg(functions.round(functions.max(s"$metric") as s"$metric", 2))
          .sort(functions.col(s"$metric"))
      }
      case _ => {
        oneTimeMetric = fullDS
          .groupBy("region")
          .agg(functions.round(functions.avg(s"$metric") as s"$metric", 2))
          .sort(functions.col(s"$metric"))
      }
    }
    oneTimeMetric
  }

  def calculateMetric(spark: SparkSession, fullDS: DataFrame, metric: String, normalizer: String, numNormalizer: Double, newName: String): DataFrame ={
    var importantData = spark.emptyDataFrame
    if (normalizer != "none"){
      importantData = fullDS
        .select("region", "date", s"$metric", s"$normalizer")
        .groupBy("region", "date")
        .agg(functions.round(functions.sum(functions.col(s"$metric")/(functions.col(s"$normalizer")/numNormalizer)), 2) as newName)
        .sort(newName)
    } else {
      importantData = fullDS
        .select("region", "date", s"$metric")
        .groupBy("region", "date")
        .agg(functions.round(functions.sum(functions.col(s"$metric")/numNormalizer), 2) as newName)
        .sort(newName)
    }
    importantData
  }

  def plotMetrics(spark: SparkSession, data: DataFrame, metric: String, filename: String): Unit ={

    import spark.implicits._

    val regionList = data.select("region").distinct().collect().map(_.getString(0))
    val dates: Array[Double] = data
      .select("date")
      .where($"region" === regionList(0))
      .distinct()
      .sort($"date")
      .rdd
      .collect()
      .map(date => DateFunc.dayInYear(date(0).asInstanceOf[String]).toDouble)


    val metricPlottable: ArrayBuffer[Array[Double]] = ArrayBuffer()
    for (region <- regionList) {
      metricPlottable.append(data
        .select(metric)
        .where($"date" =!= null)
        .where($"region" === region)
        .sort($"date")
        .rdd
        .collect
        .map(_.get(0).asInstanceOf[Double])
      )
    }
    val test1 = data
      .select(metric)
      .where($"region" === regionList(0))
      .sort($"date")
      .rdd
      .collect

    val f = Figure()
    val p = f.subplot(0)
    for (ii <- 0 to regionList.length-1) {
      p += plot(DenseVector(dates), DenseVector(metricPlottable(ii)), name = regionList(ii))
    }
    p.legend = true
    p.xlabel = "Days since 1st of January, 2020"
    p.ylabel = metric
    f.refresh()
    f.saveas(s"${filename}.png")
  }

  def changeGDP(spark: SparkSession, fullDS: DataFrame): DataFrame = {
    import spark.implicits._

    val temp = fullDS
      .withColumn("delta_gdp", (($"2020_GDP"-$"2019_GDP")/$"2019_GDP")*100)

    rankByMetric(spark, temp, "delta_gdp", "avg")
  }
}
package blue

import org.apache.spark.sql.{DataFrame, Row, SparkSession}
import org.apache.spark.sql.functions.explode

import scala.collection.mutable.ArrayBuffer

object Question8 {

  def regionCorrelation(df: DataFrame): Unit={

  }

  def regionFirstPeak(spark: SparkSession, dfRegion: DataFrame, dfCountry: DataFrame): Unit= {
    import spark.implicits._
    val dataInit = dfRegion
      .select($"name", $"countries", explode($"agg_case_data"))

    val data = dataInit
      .select($"name", $"col.date" as "date", $"col.new_cases_per_million" as "new_cases")

    val countryData = dfCountry
      .select($"name", explode($"case_data"))
      .select($"name", $"col.date" as "date", $"col.new_cases_per_million" as "new_cases")

    val regionList = data.select("name").distinct().collect().map(_.getString(0))

    var tempDates: Array[Double] = null
    var tempCases: Array[Double] = null
    var tempFrame: DataFrame = null

    val firstPeakTime: ArrayBuffer[Double] = ArrayBuffer()
    for (region <- regionList) {
      tempFrame = data.where($"name" === region)
      tempCases = tempFrame.select($"new_cases").collect().map(_.getDouble(0))
      tempDates = tempFrame.select($"date").collect().map(_.getDouble(0))
      firstPeakTime.append(StatFunc.firstPeak(tempDates, tempCases, 7, 1)._1)
    }

    val firstPeakTimeAvg: ArrayBuffer[Double] = ArrayBuffer()
    val firstPeakForCountry: ArrayBuffer[Double] = ArrayBuffer()
    var countryList: Array[Row] = Array()
    for (region <- regionList) {
      countryList = dataInit
        .select($"countries")
        .where($"name" === region)
        .collect()
      for (country <- countryList){
        tempFrame = countryData.where($"name" === country)
        tempCases = tempFrame.select($"new_cases").collect().map(_.getDouble(0))
        tempDates = tempFrame.select($"dates").collect().map(_.getDouble(0))
        firstPeakForCountry.append(StatFunc.firstPeak(tempDates, tempCases, 7, 1)._1)
      }
      firstPeakTimeAvg.append(firstPeakForCountry.sum/firstPeakForCountry.length)
      firstPeakForCountry.clear()
    }

    val firstPeakTable: ArrayBuffer[(String, Double, Double)] = ArrayBuffer()
    for (ii <- 0 to regionList.length-1){
      firstPeakTable.append((regionList(ii), firstPeakTime(ii), firstPeakTimeAvg(ii)))
    }
    println("Region\tDays to First Regional Peak\tAveraged by Country")
    for (ii <- 0 to regionList.length-1){
      println(s"${firstPeakTable(ii)._1}\t\t${firstPeakTable(ii)._2}\t\t${firstPeakTable(ii)._3}" )
    }

  }
}

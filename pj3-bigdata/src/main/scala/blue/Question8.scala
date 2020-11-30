package blue

import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable.ArrayBuffer

object Question8 {

  def regionCorrelation(spark: SparkSession, df: DataFrame): Unit={
    import spark.implicits._

    //Grab all available region names
    val regionNames = df.select("name").sort("name").distinct().rdd.map(_.get(0).toString).collect()

    for(region <- 0 to regionNames.length-1){
      //sort DataFrame according to GDP and filter by each region
      val regionArray = df.select("name", "agg_gdp", "agg_cases").
        filter($"name" === regionNames(region))
        .withColumn("agg_gdp", $"agg_gdp".cast(DoubleType))
        .withColumn("agg_cases", $"agg_cases".cast(DoubleType))
        .orderBy($"agg_gdp").rdd.collect()
      //Collect the x and y data from region and prepare it for graph/stats
      var gdpData = ArrayBuffer[Double]()
      var casesData = ArrayBuffer[Double]()
      for(i <- 0 to regionArray.length-1){
        gdpData += regionArray(i).get(1).toString.toDouble
        casesData += regionArray(i).get(2).toString.toDouble
      }
      //get the regions correlation
      val correlation = StatFunc.correlation(gdpData.toArray, casesData.toArray)
      println(s"Region ${regionNames(region)}'s GDP-Infection rate correlation: ${correlation}'")
    }
  }

  def regionFirstPeak(spark: SparkSession, dfRegion: DataFrame, dfCountry: DataFrame): Unit= {
    import spark.implicits._
    val dataInit = dfRegion
      .select($"name", $"countries", explode($"agg_case_data"))

    val data = dataInit
      .select($"name", $"col.date" as "date", $"col.total_cases_per_million" as "total_cases")

    val countryData = dfCountry
      .select($"name", explode($"case_data"))
      .select($"name", $"col.date" as "date", $"col.total_cases_per_million" as "total_cases")

    val regionList = data.select("name").distinct().collect().map(_.getString(0))

    var tempDates: Array[Double] = null
    var tempCases: Array[Double] = null
    var tempFrame: DataFrame = null

    val firstPeakTime: ArrayBuffer[Double] = ArrayBuffer()
    for (region <- regionList) {
      tempFrame = data.where($"name" === region)
      tempCases = tempFrame.select($"total_cases").collect().map(_.getDouble(0))
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
        tempCases = tempFrame.select($"total_cases").collect().map(_.getDouble(0))
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
    println("")
    for (ii <- 0 to regionList.length-1){
      println(s"${firstPeakTable(ii)._1},\t${firstPeakTable(ii)._2},\t${firstPeakTable(ii)._3}" )
    }

  }
}

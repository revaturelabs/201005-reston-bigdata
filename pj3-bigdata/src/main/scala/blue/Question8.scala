package blue

import org.apache.spark.sql.functions.explode
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.{DataFrame, Row, SparkSession}

import scala.collection.mutable.ArrayBuffer

object Question8 {

  //TODO change this to GDP vs Value of First Infection Rate Spike
  def regionCorrelation(spark: SparkSession, df: DataFrame): Unit={
    import spark.implicits._

    //Grab all available region names
    val regionNames = df.select("name").sort("name").distinct().rdd.map(_.get(0).toString).collect()

    var gdpDataRegions = ArrayBuffer[Array[Double]]()
    var casesDataRegions = ArrayBuffer[Array[Double]]()

    for(region <- 0 to regionNames.length-1){
      //Sort DataFrame according to GDP and filter by each region
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
      //Get the regions correlation
      gdpDataRegions += gdpData.toArray
      casesDataRegions += casesData.toArray
      val correlation = StatFunc.correlation(gdpData.toArray, casesData.toArray)
      println(s"Region ${regionNames(region)}'s GDP-Infection rate correlation: ${correlation}")
    }
    GraphFunc.graphSeries(gdpDataRegions.toArray,casesDataRegions.toArray,name = regionNames, style='-', legend=true)
  }

  def regionFirstPeak(spark: SparkSession, df: DataFrame): Unit= {
    import spark.implicits._

    val regionList = df.select("name").distinct().collect().map(_.getString(0))

    var tempDates: Array[Double] = null
    var tempCases: Array[Double] = null
    var tempFrame: DataFrame = null

    val firstPeakTimeAvg: ArrayBuffer[Double] = ArrayBuffer()
    val firstPeakForCountry: ArrayBuffer[Double] = ArrayBuffer()
    var countryList: Array[Row] = Array()
    for (region <- regionList) {
      countryList = df
        .select($"countries")
        .where($"name" === region)
        .collect()
      for (country <- countryList){
        tempFrame = df.where($"name" === country)
        tempCases = tempFrame.select($"total_cases").collect().map(_.getDouble(0))
        tempDates = tempFrame.select($"dates").collect().map(_.getDouble(0))
        firstPeakForCountry.append(StatFunc.firstPeak(tempDates, tempCases, 7, 1)._1)
      }
      firstPeakTimeAvg.append(firstPeakForCountry.sum/firstPeakForCountry.length)
      firstPeakForCountry.clear()
    }

    val firstPeakTable: ArrayBuffer[(String, Double)] = ArrayBuffer()
    for (ii <- 0 to regionList.length-1){
      firstPeakTable.append((regionList(ii), firstPeakTimeAvg(ii)))
    }
    println("")
    for (ii <- 0 to regionList.length-1){
      println(s"${firstPeakTable(ii)._1},\t${firstPeakTable(ii)._2}}" )
    }
  }
}

package blue

import org.apache.spark.sql.functions.{avg, explode}
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.{DataFrame, Row, SaveMode, SparkSession}

import scala.collection.mutable.ArrayBuffer

object Question8 {

  //TODO change this to GDP vs Value of First Infection Rate Spike
  def regionCorrelation(spark: SparkSession, df: DataFrame): Unit={
    import spark.implicits._

    val regionNames = df.select("region").sort("region").distinct().rdd.map(_.get(0).toString).collect()
    var regionGDP = ArrayBuffer[ArrayBuffer[Double]]()
    var regionPeaks = ArrayBuffer[ArrayBuffer[Double]]()


    for(region <- (0 to regionNames.length)){
      var gdp = ArrayBuffer[Double]()
      var peak = ArrayBuffer[Double]()
      val specificRegion = regionNames(region)
      val regionCountries = df.select("country").filter($"region" === regionNames(region)).distinct().rdd.map(_.get(0).toString).collect()
      // Get the first peak for each country in region and gdp
      for(country <- (0 to regionCountries.length-1)){
        val countryDF = df.where($"country" === regionCountries(country)).sort("date").filter($"date" =!= "NULL")
        val tempCases = countryDF.select($"new_cases").collect().map(_.get(0).toString.toDouble)
        val tempDates = countryDF.select($"date").collect().map(_.get(0).toString).map(DateFunc.dayInYear(_).toDouble)
        peak += (StatFunc.firstPeak(tempDates, tempCases, 7, 1)._1)

        val tempGDP = countryDF.select($"gdp_currentPrices").filter($"year" === "2020" && $"gdp_currentPrices" =!= "NULL")
        val avgGDP = tempGDP.select(avg($"gdp_currentPrices")).collect().map(_.getDouble(0))
        gdp += avgGDP(0)
      }
      // Give correlation for each region
      println(s"Region ${regionNames(region)}'s GDP - First New Cases Peak Potency Correlation: ${StatFunc.correlation(gdp.toArray,peak.toArray)}")
      // Append arrays to use for graphing
      regionGDP += gdp
      regionPeaks += peak
    }

    //Graph here
    /*
    GraphFunc.graphSeries(gdpDataRegions.toArray,casesDataRegions.toArray,name = regionNames, style='-', legend=true)*/
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

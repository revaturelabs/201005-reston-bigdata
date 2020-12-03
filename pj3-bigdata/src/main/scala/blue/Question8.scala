package blue

import org.apache.spark.sql.functions.{avg, explode}
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.{DataFrame, Row, SaveMode, SparkSession}

import scala.collection.mutable.ArrayBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

object Question8 {

  //TODO change this to GDP vs Value of First Infection Rate Spike
  def regionCorrelation(spark: SparkSession, df: DataFrame): Unit={
    import spark.implicits._
    df.write.partitionBy("region").bucketBy(15, "country").saveAsTable("dfOptimize")

    val regionNames = spark.sql("SELECT DISTINCT region FROM dfOptimize ORDER BY region").rdd.map(_.get(0).toString).collect()

    //val regionNames = df.select("region").sort("region").distinct().rdd.map(_.get(0).toString).collect()
    var regionGDP = ArrayBuffer[Array[Double]]()
    var regionPeaks = ArrayBuffer[Array[Double]]()


    for(region <- (0 to regionNames.length-1)){
      var gdp = ArrayBuffer[Double]()
      var peak = ArrayBuffer[Double]()
      val specificRegion = regionNames(region)
      val regionCountries = spark.sql(s"SELECT DISTINCT country, region FROM dfOptimize WHERE region = '$specificRegion' ").rdd.map(_.get(0).toString).collect()
     // val regionCountries = df.select("country").filter($"region" === regionNames(region)).distinct().rdd.map(_.get(0).toString).collect()
      // Get the first peak for each country in region and gdp
      for (country <- (0 to regionCountries.length-1)) {
          val regionCountry = regionCountries(country)
          val countryDF = spark.sql(s"SELECT DISTINCT * FROM dfOptimize WHERE country = '$regionCountry'" +
            s" AND date != 'NULL' " +
            s" AND year = '2020'" +
            s" AND current_prices_gdp != 'NULL'")
          //val countryDF = df.where($"country" === regionCountries(country)).sort("date").filter($"date" =!= "NULL" && $"year" === "2020" && $"current_prices_gdp" =!= "NULL")
          val tempCases = countryDF.select($"new_cases").collect().map(_.get(0).toString.toDouble)
          val tempDates = countryDF.select($"date").collect().map(_.get(0).toString).map(DateFunc.dayInYear(_).toDouble)
          if(tempDates.length > 0 && tempCases.length > 0) {
            peak += (StatFunc.firstPeak(tempDates, tempCases, 3, .5)._1)

            val tempGDP = countryDF.select($"current_prices_gdp")
            val avgGDP = tempGDP.select(avg($"current_prices_gdp")).collect().map(_.getDouble(0))
            gdp += avgGDP(0)
          }
      }

      // Give correlation for each region
      println(s"Region ${regionNames(region)}'s GDP - First New Cases Peak Potency Correlation: ${StatFunc.correlation(gdp.toArray,peak.toArray)}")
      // Append arrays to use for graphing
      regionGDP += gdp.toArray
      regionPeaks += peak.toArray
    }
    spark.sql("DROP TABLE IF EXISTS dfOptimize")
    //Graph here
    //GraphFunc.graphSeries(regionGDP.toArray,regionPeaks.toArray, style='-', name = regionNames, legend=true)
  }


  def regionFirstPeak(spark: SparkSession, df: DataFrame): Unit= {
    import spark.implicits._
    df.write.partitionBy("region").bucketBy(30, "country").saveAsTable("dfOptimize")

    val regionList = spark.sql("SELECT DISTINCT region FROM dfOptimize ORDER BY region").rdd.map(_.get(0).toString).collect()
    var tempDates: Array[Double] = null
    var tempCases: Array[Double] = null
    var tempFrame: DataFrame = null

    val firstPeakTimeAvg: ArrayBuffer[Double] = ArrayBuffer()
    val firstPeakForCountry: ArrayBuffer[Double] = ArrayBuffer()
    var countryList: Array[String] = Array()
    for (region <- regionList) {
      countryList = df
        .select($"country")
        .where($"region" === region)
        .distinct()
        .collect()
        .map(_.get(0).asInstanceOf[String])
      for (country <- countryList){
//        tempFrame = df.select($"country", $"date", $"new_cases").where($"country" === country).filter($"date" =!= "NULL").distinct().sort("date").cache()
        tempFrame = spark.sql(s"SELECT DISTINCT * FROM dfOptimize WHERE country = '$country' AND date != 'NULL' ORDER BY date ").cache()
        tempCases = tempFrame.select($"new_cases").collect().map(_.get(0).toString.toDouble)
        tempDates = tempFrame.select($"date").collect().map(_.get(0).toString).map(DateFunc.dayInYear(_).toDouble)
        firstPeakForCountry.append(StatFunc.firstPeakMod(tempDates, tempCases, 7, 5)._1)
        println(s"${country}: ${firstPeakForCountry.last}")
      }
      firstPeakTimeAvg.append(firstPeakForCountry.sum/firstPeakForCountry.length)
      println(s"\t\t${region}: ${firstPeakTimeAvg.last}")
      firstPeakForCountry.clear()
    }

    val firstPeakTable: ArrayBuffer[(String, Double)] = ArrayBuffer()
    for (ii <- 0 to regionList.length-1){
      firstPeakTable.append((regionList(ii), firstPeakTimeAvg(ii)))
    }
    println("")
    for (ii <- 0 to regionList.length-1){
      println(s"${firstPeakTable(ii)._1}, ${firstPeakTable(ii)._2}}" )
    }
    spark.sql("DROP TABLE IF EXISTS dfOptimize")
  }
}

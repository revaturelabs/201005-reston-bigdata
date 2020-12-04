package blue

import java.io.PrintWriter
import java.nio.file.{Files, Paths}

import org.apache.spark.sql.functions.{avg, explode}
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.{DataFrame, Row, SaveMode, SparkSession}

import scala.collection.mutable.ArrayBuffer
object Question8 {

  //TODO change this to GDP vs Value of First Infection Rate Spike
  def regionCorrelation(spark: SparkSession, df: DataFrame): Unit={
    import spark.implicits._
    val tableName = "dfOptimize"
    //df.write.mode("overwrite").partitionBy("region").bucketBy(40, "country")saveAsTable(tableName)

    val regionNames = spark.sql(s"SELECT DISTINCT region FROM $tableName ORDER BY region").rdd.map(_.get(0).toString).collect()

    //val regionNames = df.select("region").sort("region").distinct().rdd.map(_.get(0).toString).collect()

    for(region <- (0 to regionNames.length-1)){
      var gdp = ArrayBuffer[Double]()
      var peak = ArrayBuffer[Double]()
      val specificRegion = regionNames(region)
      val regionCountries = spark.sql(s"SELECT DISTINCT country, region FROM $tableName WHERE region = '$specificRegion' ").rdd.map(_.get(0).toString).collect()
     // val regionCountries = df.select("country").filter($"region" === regionNames(region)).distinct().rdd.map(_.get(0).toString).collect()
      // Get the first peak for each country in region and gdp
      for (country <- (0 to regionCountries.length-1)) {
          val regionCountry = regionCountries(country)
          val countryDF = spark.sql(s"SELECT DISTINCT * FROM $tableName WHERE country = '$regionCountry'" +
            s" AND date != 'NULL' " +
            s" AND year = '2020'" +
            s" AND current_prices_gdp != 'NULL'" +
            s" ORDER BY date").cache()
          //val countryDF = df.where($"country" === regionCountries(country)).sort("date").filter($"date" =!= "NULL" && $"year" === "2020" && $"current_prices_gdp" =!= "NULL")
          val tempCases = countryDF.select($"new_cases").collect().map(_.get(0).toString.toDouble)
          val tempDates = countryDF.select($"date").collect().map(_.get(0).toString).map(DateFunc.dayInYear(_).toDouble)
          if(tempDates.length > 0 && tempCases.length > 0) {
            peak += (StatFunc.firstMajorPeak(tempDates, tempCases, 7, 10, 5)._2)
            val tempGDP = countryDF.select($"current_prices_gdp")
            val avgGDP = tempGDP.select(avg($"current_prices_gdp")).collect().map(_.getDouble(0))
            gdp += avgGDP(0)
          }
      }

      // Give correlation for each region
      println(s"Region ${regionNames(region)}'s GDP - First Major Peak New Cases Value Correlation: ${StatFunc.correlation(gdp.toArray,peak.toArray)}")
    }
    spark.sql(s"DROP TABLE IF EXISTS $tableName")
    //Graph here
    //GraphFunc.graphSeries(regionGDP.toArray,regionPeaks.toArray, style='-', name = regionNames, legend=true)
  }


  def regionFirstPeak(spark: SparkSession, df: DataFrame, resultpath: String): Unit= {
    import spark.implicits._

    val rRWriter = new PrintWriter(Paths.get(s"${resultpath}/region_by_time_2_first_peak.tmp").toFile)
    df.write.partitionBy("region").bucketBy(40, "country").saveAsTable("dfOptimize")

    val regionList = spark.sql("SELECT DISTINCT region FROM dfOptimize ORDER BY region").rdd.map(_.get(0).toString).collect()
    var tempDates: Array[Double] = null
    var tempCases: Array[Double] = null
    var tempFrame: DataFrame = null

    val firstPeakTimeAvg: ArrayBuffer[Double] = ArrayBuffer()
    val firstPeakForCountry: ArrayBuffer[Double] = ArrayBuffer()
    var countryList: Array[String] = Array()
    var peakTime: Double = 0
    for (region <- regionList) {
      val rCWriter = new PrintWriter(Paths.get(s"${resultpath}/${region}.tmp").toFile)
      countryList = df
        .select($"country")
        .where($"region" === region)
        .distinct()
        .collect()
        .map(_.get(0).asInstanceOf[String])
      for (country <- countryList){
        tempFrame = spark.sql(s"SELECT DISTINCT country, date, new_cases FROM dfOptimize WHERE country = '$country' AND date != 'NULL' ").sort($"date").cache()
        tempCases = tempFrame.select($"new_cases").collect().map(_.get(0).toString.toDouble)
        tempDates = tempFrame.select($"date").collect().map(_.get(0).toString).map(DateFunc.dayInYear(_).toDouble)
        peakTime = StatFunc.firstMajorPeak(tempDates, tempCases, 7, 10, 5)._1
        if (peakTime != -1) {
          firstPeakForCountry.append(peakTime)
//          println(s"${country}, ${firstPeakForCountry.last}")
          rCWriter.println(s"${country}, ${firstPeakForCountry.last}")
        }
      }
      firstPeakTimeAvg.append(firstPeakForCountry.sum/firstPeakForCountry.length)
//      println(s"\t\t${region}: ${firstPeakTimeAvg.last}")
      rRWriter.println(s"${region}, ${firstPeakTimeAvg.last}")
      firstPeakForCountry.clear()
      rCWriter.close()
      Files.move(
        Paths.get(s"${resultpath}/${region}.tmp"),
        Paths.get(s"${resultpath}/${region}.csv")
      )
    }
    rRWriter.close()
    Files.move(
      Paths.get(s"${resultpath}/region_by_time_2_first_peak.tmp"),
      Paths.get(s"${resultpath}/region_by_time_2_first_peak.csv")
    )
//    val firstPeakTable: ArrayBuffer[(String, Double)] = ArrayBuffer()
//    for (ii <- 0 to regionList.length-1){
//      firstPeakTable.append((regionList(ii), firstPeakTimeAvg(ii)))
//    }
//    println("")
//    for (ii <- 0 to regionList.length-1){
//      println(s"${firstPeakTable(ii)._1}, ${firstPeakTable(ii)._2}}" )
//    }
    spark.sql("DROP TABLE IF EXISTS dfOptimize")
  }
}

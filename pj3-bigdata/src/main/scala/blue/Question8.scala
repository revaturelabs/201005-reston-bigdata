package blue

import java.io.PrintWriter
import java.nio.file.{Files, Paths}
import java.util.Calendar

import org.apache.spark.sql.functions.{avg, explode}
import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.{DataFrame, Row, SaveMode, SparkSession}

import scala.collection.mutable.ArrayBuffer
object Question8 {

  //TODO change this to GDP vs Value of First Infection Rate Spike
  def regionCorrelation(spark: SparkSession, df: DataFrame): Unit={
    import spark.implicits._
    val now = Calendar.getInstance()
    val time = now.getTimeInMillis()
    val tableName = s"dfOptimize$time"
    df.write.mode("overwrite").partitionBy("region").bucketBy(40, "country").saveAsTable(tableName)

    val regionNames = spark.sql(s"SELECT DISTINCT region FROM $tableName ORDER BY region").rdd.map(_.get(0).toString).collect()


    for(region <- (0 to regionNames.length-1)){
      var gdp = ArrayBuffer[Double]()
      var peak = ArrayBuffer[Double]()
      val specificRegion = regionNames(region)

      val regionCountries = spark.sql(s"SELECT DISTINCT country, region FROM $tableName WHERE region = '$specificRegion' ").rdd.map(_.get(0).toString).collect()
      // Get the first peak for each country in region and gdp
      for (country <- (0 to regionCountries.length-1)) {
        val regionCountry = regionCountries(country)
        val countryDF = spark.sql(s"SELECT DISTINCT date, new_cases_per_million, gdp_per_capita FROM $tableName WHERE country = '$regionCountry'" +
          s" AND date != 'NULL' " +
          s" AND year = '2020'" +
          s" AND gdp_per_capita != 'NULL'" +
          s" ORDER BY date")
          .cache()


        val tempCases = countryDF.select($"new_cases_per_million").collect().map(_.get(0).toString.toDouble)
        val tempDates = countryDF.select($"date").collect().map(_.get(0).toString).map(DateFunc.dayInYear(_).toDouble)
        if(tempDates.length > 0 && tempCases.length > 0) {
          peak += (StatFunc.firstMajorPeak(tempDates, tempCases, 7, 10, 5)._2)
          val tempGDP = countryDF.select($"gdp_per_capita")
          val avgGDP = tempGDP.select(avg($"gdp_per_capita")).collect().map(_.get(0).toString.toDouble)
          gdp += avgGDP(0)
        }
      }

      // Give correlation for each region
      println(s"Region ${regionNames(region)}'s GDP - First Major Peak New Cases Value Correlation: ${StatFunc.correlation(gdp.toArray,peak.toArray)}")
    }

    spark.sql(s"DROP TABLE IF EXISTS $tableName")
  }


  def regionFirstPeak(spark: SparkSession, df: DataFrame, resultpath: String): Unit= {
    import spark.implicits._
    val now = Calendar.getInstance()
    val time = now.getTimeInMillis()
    val tableName = s"dfOptimize$time"

    df.write.partitionBy("region").bucketBy(40, "country").saveAsTable(tableName)

    val regionList = spark.sql(s"SELECT DISTINCT region FROM $tableName ORDER BY region").rdd.map(_.get(0).toString).collect()
    var tempDates: Array[Double] = null
    var tempCases: Array[Double] = null
    var tempFrame: DataFrame = null

    val firstPeakTimeAvg: ArrayBuffer[Double] = ArrayBuffer()
    val firstPeakForCountry: ArrayBuffer[Double] = ArrayBuffer()
    var countryList: Array[String] = Array()
    var peakTime: Double = 0
    for (region <- regionList) {
      countryList = df
        .select($"country")
        .where($"region" === region)
        .distinct()
        .collect()
        .map(_.get(0).asInstanceOf[String])
      for (country <- countryList) {
        tempFrame = spark.sql(s"SELECT DISTINCT country, date, new_cases FROM $tableName WHERE country = '$country' AND date != 'NULL' ").sort($"date").cache()
        tempCases = tempFrame.select($"new_cases").collect().map(_.get(0).toString.toDouble)
        tempDates = tempFrame.select($"date").collect().map(_.get(0).toString).map(DateFunc.dayInYear(_).toDouble)
        peakTime = StatFunc.firstMajorPeak(tempDates, tempCases, 7, 10, 5)._1
        if (peakTime != -1) {
          firstPeakForCountry.append(peakTime)
        }
      }
      firstPeakTimeAvg.append(firstPeakForCountry.sum / firstPeakForCountry.length)
      println(s"${region} Average Days to First Major Peak: ${firstPeakTimeAvg.last}")
      firstPeakForCountry.clear()
    }
    spark.sql(s"DROP TABLE IF EXISTS $tableName")
  }

}

package blue

import org.apache.spark.sql.types.DoubleType
import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable.ArrayBuffer

object Question8 {

  def regionCorrelation(spark: SparkSession, df: DataFrame): Unit={
    import spark.implicits._

    //Grab all available region names
    val regionNames = df.select("name").sort("name").distinct().rdd.map(_.get(0).toString).collect()

    var gdpDataRegions = ArrayBuffer[Array[Double]]()
    var casesDataRegions = ArrayBuffer[Array[Double]]()

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
      gdpDataRegions += gdpData.toArray
      casesDataRegions += casesData.toArray
      val correlation = StatFunc.correlation(gdpData.toArray, casesData.toArray)
      println(s"Region ${regionNames(region)}'s GDP-Infection rate correlation: ${correlation}'")
    }
    GraphFunc.graphSeries(gdpDataRegions.toArray,casesDataRegions.toArray,name = regionNames, style='-', legend=true)
  }


  def regionFirstPeak(spark: SparkSession, df: DataFrame): Unit= {

  }
}

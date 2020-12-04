package blue

import java.io.PrintWriter
import java.nio.file.Paths
import org.apache.spark.sql.functions.{max, sum}

import org.apache.spark.sql.{DataFrame, SparkSession}

object Question1 {
  def initialSolution(spark: SparkSession, origdata: DataFrame, resultpath: String): Unit ={
    import spark.implicits._
    val pop_col = origdata
      .withColumn("population",$"total_cases"/$"total_cases_per_million")
      .groupBy("region", "country")
      .agg(max($"population") as "population")
      .groupBy("region")
      .agg(sum($"population") as "population")
    val data = origdata.join(pop_col, "region")

    println("")
    println("Average New Cases per Day in Each Region")
    RankRegions.rankByMetricLow(spark, data, "new_cases", "avg").show()
    println("")
    println("Average New Cases per Million People per Day in Each Region (normalized before region grouping)")
    RankRegions.rankByMetricLow(spark, data, "new_cases_per_million", "avg").show()
    println("")
    println("Average New Cases per Million People per Day in Each Region")
    RankRegions.rankByMetricLow(spark, data, "new_cases", "pop").show()

    println("")
    println("Total Cases in Each Region")
    RankRegions.rankByMetricLow(spark, data, "total_cases", "max").show()
    println("")
    println("Total Cases per Million People in Each Region (normalized before region grouping)")
    RankRegions.rankByMetricLow(spark, data, "total_cases_per_million", "max").show()
    println("")
    println("Total Cases per Million People in Each Region")
    RankRegions.rankByMetricLow(spark, data, "total_cases_per_million", "maxpop").show()

//    RankRegions.plotMetrics(spark, data, "new_cases_per_million", s"${resultpath}/plot_infection_rate_per_million")
//    RankRegions.plotMetrics(spark, data, "new_cases", s"${resultpath}/plot_infection_rate")
//    RankRegions.plotMetrics(spark, data, "total_cases", s"${resultpath}/plot_total_infections")
//    RankRegions.plotMetrics(spark, data, "total_cases_per_million", s"${resultpath}/plot_total_infections_per_million")

    println("")
    println("Average GDP Percent Change in Each Region")
    RankRegions.changeGDP(spark, data, "current_prices_gdp", false).show()

    println("")
    println("Average GDP per Capita Percent Change in Each Region")
    RankRegions.changeGDP(spark, data, "gdp_per_capita", false).show()

//    println("")
//    println("Average GDP per Capita Percent Change in Each Region ()")
//    RankRegions.changeGDP(spark, data, "current_prices_gdp", true).show()
  }
}
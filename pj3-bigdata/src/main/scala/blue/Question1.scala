package blue

import java.io.PrintWriter
import java.nio.file.Paths

import org.apache.spark.sql.{DataFrame, SparkSession}

object Question1 {
  def initialSolution(spark: SparkSession, data: DataFrame, resultpath: String): Unit ={
    val resultpath = "results"
    val regionByInfectionRate = RankRegions.rankByMetricLow(spark, data, "new_cases_per_million")
    regionByInfectionRate.coalesce(1).write.csv(s"${resultpath}/csv_avg_infection_rate_per_million")
    RankRegions.rankByMetricLow(spark, data, "new_cases")
      .coalesce(1).write.csv(s"${resultpath}/csv_avg_infection_rate")
    RankRegions.plotMetrics(spark, data, "new_cases_per_million", s"${resultpath}/plot_infection_rate_per_million")
    RankRegions.plotMetrics(spark, data, "new_cases", s"${resultpath}/plot_infection_rate")
    RankRegions.plotMetrics(spark, data, "total_cases", s"${resultpath}/plot_total_infections")
    RankRegions.plotMetrics(spark, data, "total_cases_per_million", s"${resultpath}/plot_total_infections_per_million")

    e.current_prices_gdp, e.gdp_per_capita,
    val regionByGDPFull = RankRegions.changeGDP(spark, data, "current_prices_gdp")
    regionByGDPFull.coalesce(1).write.csv(s"${resultpath}/csv_regions_by_gdp")
    RankRegions.changeGDP(spark, data, "gdp_per_capita")
      .coalesce(1).write.csv(s"${resultpath}/csv_regions_by_gdp_per_capita")
  }
}
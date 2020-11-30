package blue

import org.apache.spark.sql.functions.explode
import org.apache.spark.sql.{DataFrame, SparkSession}

object Question1 {
  def initialSolution(spark: SparkSession, originalData: DataFrame): Unit ={
	import spark.implicits._
    val data = originalData
      .select($"name", $"agg_population", $"agg_gdp", explode($"agg_case_data"))
      .select($"name", $"agg_population", $"agg_gdp" as "GDP", $"col.date" as "date", $"col.new_cases" as "new_cases")
	  
    val regionByInfectionRateFull = RankRegions.calculateMetric(spark, data, "new_cases", "agg_population",
      100000, "infections_per_pop_100k")

    val regionByInfectionRate = RankRegions.latestRankByMetric(spark, data, "new_cases", "agg_population",
      100000, "infections_per_pop_100k")
    regionByInfectionRate.show()

    val regionByGDPFull = RankRegions.calculateMetric(spark, data, "GDP", "none",
      1, "GDP")

    val regionByGDP = RankRegions.latestRankByMetric(spark, data, "GDP", "none",
      1, "GDP")
    regionByGDP.show()

    RankRegions.plotMetrics(spark, regionByInfectionRateFull, "infections_per_pop_100k", "infections")

    RankRegions.plotMetrics(spark, regionByGDPFull, "GDP", "GDP")
  }
}
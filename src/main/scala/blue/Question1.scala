package blue

import org.apache.spark.sql.{DataFrame, SparkSession}

object Question1 {
  def initialSolution(spark: SparkSession, data: DataFrame): Unit ={
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
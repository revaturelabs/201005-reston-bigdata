package blue

import org.apache.spark.sql.{DataFrame, SparkSession}

object Question1 {
  def initialSolution(spark: SparkSession, data: DataFrame): Unit ={
    val regionByInfectionRateFull = rankRegionsByMetric.calculateMetric(spark, data, "new_cases", "agg_population",
      100000, "infections_per_pop_100k")

    val regionByInfectionRate = rankRegionsByMetric.latestRankByMetric(spark, data, "new_cases", "agg_population",
      100000, "infections_per_pop_100k")
    regionByInfectionRate.show()

    val regionByGDPFull = rankRegionsByMetric.calculateMetric(spark, data, "GDP", "none",
      1, "GDP")

    val regionByGDP = rankRegionsByMetric.latestRankByMetric(spark, data, "GDP", "none",
      1, "GDP")
    regionByGDP.show()

    rankRegionsByMetric.plotMetrics(spark, regionByInfectionRateFull, "infections_per_pop_100k", "infections")

    rankRegionsByMetric.plotMetrics(spark, regionByGDPFull, "GDP", "GDP")
  }
}
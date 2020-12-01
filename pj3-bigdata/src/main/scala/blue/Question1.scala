package blue

import org.apache.spark.sql.{DataFrame, SparkSession}

object Question1 {
  def initialSolution(spark: SparkSession, casedata: DataFrame, econdata: DataFrame): Unit ={

    val regionByInfectionRateFull = RankRegions.calculateMetric(spark, casedata, "new_cases", "agg_population",
      100000, "infections_per_pop_100k")

    val regionByInfectionRate = RankRegions.rankByMetric(spark, regionByInfectionRateFull, "infections_per_pop_100k")
    regionByInfectionRate.show()

    RankRegions.plotMetrics(spark, regionByInfectionRateFull, "infections_per_pop_100k", "infections")

    val regionByGDPFull = RankRegions.calculateChange(spark, econdata, "gdp")

    val regionByGDP = RankRegions.rankByMetric(spark, regionByGDPFull, "d_percent_gdp")
    regionByGDP.show()

  }
}
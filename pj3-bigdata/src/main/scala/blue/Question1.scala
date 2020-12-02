package blue

import org.apache.spark.sql.{DataFrame, SparkSession}

object Question1 {
  def initialSolution(spark: SparkSession, data: DataFrame): Unit ={

//    val regionByInfectionRateFull = RankRegions.calculateMetric(spark, casedata, "new_cases", "agg_population",
//      100000, "infections_per_pop_100k")

    val regionByInfectionRate = RankRegions.rankByMetricLow(spark, data, "new_cases_per_million")
    regionByInfectionRate.show()

    RankRegions.plotMetrics(spark, data, "new_cases_per_million", "infections")

    val regionByGDPFull = RankRegions.changeGDP(spark, data)
    regionByGDPFull.show()
    regionByInfectionRate.show()
  }
}
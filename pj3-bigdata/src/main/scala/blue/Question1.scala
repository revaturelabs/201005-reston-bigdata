package blue

import org.apache.spark.sql.{DataFrame, SparkSession}

object Question1 {
  def initialSolution(spark: SparkSession, data: DataFrame): Unit ={

    val regionByInfectionRate = RankRegions.rankByMetricLow(spark, data, "new_cases_per_million")
    regionByInfectionRate.show()

    RankRegions.plotMetrics(spark, data, "new_cases_per_million", "infection_rate_per_million")
    RankRegions.plotMetrics(spark, data, "new_cases", "infection_rate")
    RankRegions.plotMetrics(spark, data, "total_cases", "total_infections")
    RankRegions.plotMetrics(spark, data, "total_cases_per_million", "infections_per_million")

//    val regionByGDPFull = RankRegions.changeGDP(spark, data)
//    regionByGDPFull.show()
//    regionByInfectionRate.show()
  }
}
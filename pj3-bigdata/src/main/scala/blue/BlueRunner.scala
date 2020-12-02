package blue

import org.apache.spark.sql.SparkSession

object BlueRunner extends App {
  val spark = SparkSession.builder()
    .master("local[4]")
    .getOrCreate()


  val regionDF = spark.read.json("regionDict")
  regionDF.show()
//  val econRawDF = spark.read.option("delimiter","\t").option("header",true).csv("C:/Users/liamh/Project_3/201005-reston-bigdata/WorldEconomicData_AllCountries_Test.tsv")
////  val econRawDF = spark.read.option("delimiter","\t").option("header",true).csv("C:/Users/river/IdeaProjects/201005-reston-bigdata/WorldEconomicData_AllCountries_Test.tsv")
////  val caseRawDF = spark.read.csv("C")
////  val econRawDF = spark.read.option("delimiter","\t").option("header",true)
////  .csv("s3://adam-king-848/data/WorldEconomicData_AllCountries_Test.tsv")
//  // s3 path s3://adam-king-848/data/
//  val caseRawDF = spark.read.option("delimiter","\t").option("header",true)
//    .csv("daily_stats.tsv")
//
//  val caseModDF = caseRawDF.withColumnRenamed("country", "name")
////  econRawDF.show()
////  caseModDF.show()
//  val regionByInfectionRateFull = RankRegions.calculateMetric(spark, caseModDF, "new_cases_per_million", "none",
//    1, "infections_rate")
//  val regionByInfectionRate = RankRegions.rankByMetric(spark, regionByInfectionRateFull, "infections_rate")
//  regionByInfectionRate.show()
//  RankRegions.plotMetrics(spark, regionByInfectionRateFull, "infections_rate", "infections")



println("Hello World")
}

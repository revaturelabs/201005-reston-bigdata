package blue

import org.apache.spark.sql.SparkSession

object BlueRunner extends App {
  val spark = SparkSession.builder()
    .master("local[4]")
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  val regionDF = spark.read.json("regionDict")
  regionDF.show()

 // val econRawDF = spark.read.option("delimiter","\t").option("header",true).csv("C:/Users/liamh/Project_3/201005-reston-bigdata/WorldEconomicData_AllCountries_Test.tsv")
  val econRawDF = spark.read.option("delimiter","\t").option("header",true).csv("C:/Users/river/IdeaProjects/201005-reston-bigdata/WorldEconomicData_AllCountries_Test.tsv")

//  val caseRawDF = spark.read.csv("C")
//  val econRawDF = spark.read.option("delimiter","\t").option("header",true)
//  .csv("s3://adam-king-848/data/WorldEconomicData_AllCountries_Test.tsv")
  // s3 path s3://adam-king-848/data/
  val caseRawDF = spark.read.option("delimiter","\t").option("header",true)
    .csv("daily_stats.tsv")

//  regionDF.printSchema()
//  econRawDF.printSchema()
//  caseRawDF.printSchema()
  val caseRegionDF = DataFrameManipulator.caseJoin(spark, regionDF, caseRawDF)
//  caseRegionDF.printSchema()
  caseRegionDF.show()
  val econRegionDF = DataFrameManipulator.econJoin(spark, regionDF, econRawDF)
//  econRegionDF.printSchema()
//  econRegionDF.show()
  val fullDF = DataFrameManipulator.joinCaseEcon(spark, caseRegionDF, econRegionDF)
//  fullDF.printSchema()
//  fullDF.show()

  Question8.regionCorrelation(spark,fullDF)
//  val regionByInfectionRateFull = RankRegions.calculateMetric(spark, caseModDF, "new_cases_per_million", "none",
//    1, "infections_rate")
//  val regionByInfectionRate = RankRegions.rankByMetric(spark, regionByInfectionRateFull, "infections_rate")
//  regionByInfectionRate.show()
//  RankRegions.plotMetrics(spark, regionByInfectionRateFull, "infections_rate", "infections")



println("Hello World")
}

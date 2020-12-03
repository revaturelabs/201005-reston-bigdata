package blue

import org.apache.spark.sql.SparkSession

object BlueRunner extends App {
  val spark = SparkSession.builder()
    .master("local[4]")
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")


  val regionDF = spark.read.json("regionDict")
//  regionDF.show()
//  val econRawDF = spark.read.option("delimiter","\t").option("header",true).csv("C:/Users/liamh/Project_3/201005-reston-bigdata/WorldEconomicData_AllCountries_Test.tsv")

  val econRawDF = spark.read.option("delimiter","\t").option("header",true).csv("C:/Users/liamh/Project_3/201005-reston-bigdata/economic_data_2018-2021.tsv")
//  val econRawDF = spark.read.option("delimiter","\t").option("header",true).csv("C:/Users/river/IdeaProjects/201005-reston-bigdata/economic_data_2018-2021.tsv")

  val caseRawDF = spark.read.option("delimiter","\t").option("header",true)
    .csv("daily_stats.tsv")

  regionDF.printSchema()
  econRawDF.printSchema()
  caseRawDF.printSchema()
  val caseRegionDF = DataFrameManipulator.caseJoin(spark, regionDF, caseRawDF)
//  caseRegionDF.printSchema()
//  caseRegionDF.show()
  val econRegionDF = DataFrameManipulator.econJoin(spark, regionDF, econRawDF)
//  econRegionDF.printSchema()
//  econRegionDF.show()
  val fullDF = DataFrameManipulator.joinCaseEcon(spark, caseRegionDF, econRegionDF)
//  fullDF.printSchema()
//  fullDF.show()

//  Question8.regionCorrelation(spark, fullDF)
  Question8.regionFirstPeak(spark, fullDF)
}

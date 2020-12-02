package blue

import org.apache.spark.sql.SparkSession

object BlueRunner extends App {
  val spark = SparkSession.builder()
    .master("local[4]")
    .getOrCreate()

//  val econRawDF = spark.read.option("delimiter","\t").option("header",true).csv("C:/Users/river/IdeaProjects/201005-reston-bigdata/WorldEconomicData_AllCountries_Test.tsv")
//  val caseRawDF = spark.read.csv("C")
//  val econRawDF = spark.read.option("delimiter","\t").option("header",true)
//  .csv("s3://adam-king-848/data/WorldEconomicData_AllCountries_Test.tsv")
  val caseRawDF = spark.read.option("delimiter","\t").option("header",true)
    .csv("s3://adam-king-848/data/daily_stats.tsv")

//  econRawDF.show()
  caseRawDF.show()

println("Hello World")
}

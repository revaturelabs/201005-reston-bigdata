package blue

import org.apache.spark.sql.SparkSession

object BlueRunner extends App {
  val spark = SparkSession.builder()
    .master("local[4]")
    .config("spark.hadoop.fs.s3a.access.key", "AKIA4OK5FKIYQUHKGGEP")
    .config("spark.hadoop.fs.s3a.secret.key", "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")
    .config("spark.hadoop.fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
    .getOrCreate()


  val econRawDF = spark.read.option("delimiter","\t").option("header",true).csv("C:/Users/river/IdeaProjects/201005-reston-bigdata/WorldEconomicData_AllCountries_Test.tsv")

  econRawDF.show()
  //caseRawDF.show()

println("Hello World")
}

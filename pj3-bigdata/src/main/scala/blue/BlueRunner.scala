package blue

import org.apache.spark.sql.SparkSession

object BlueRunner extends App {
  val spark = SparkSession.builder()
    .master("local[4]")
    .getOrCreate()

  val econRawDF = spark.read.load()
  val caseRawDF = spark.read.load()


println("Hello World")
}

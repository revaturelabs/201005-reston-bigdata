package blue


import org.apache.spark.sql.{AnalysisException, DataFrame, SparkSession}

object BlueRunner  {

  val spark = SparkSession.builder()
    .master("local[4]")
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  //Type path here
  val casepath="C:\\Users\\river\\IdeaProjects\\201005-reston-bigdata\\pj3-bigdata\\daily_stats.tsv"
  val econpath="C:\\Users\\river\\IdeaProjects\\201005-reston-bigdata\\economic_data_2018-2021.tsv"

  //TODO delete this main method
  def main(args: Array[String]): Unit = {
    Q8_1(spark)
  }

  def df(spark: SparkSession, econpath:String,casepath:String): DataFrame  ={
    val regionDF = spark.read.json("regionDict")
    val econRawDF = spark.read.option("delimiter","\t").option("header",true).csv(econpath)
    val caseRawDF = spark.read.option("delimiter","\t").option("header",true)
      .csv(casepath)
    val caseRegionDF = DataFrameManipulator.caseJoin(spark, regionDF, caseRawDF)
    val econRegionDF = DataFrameManipulator.econJoin(spark, regionDF, econRawDF)
    val fullDF = DataFrameManipulator.joinCaseEcon(spark, caseRegionDF, econRegionDF)
    fullDF
  }

  def Q1(spark:SparkSession, fullDF: DataFrame=df(spark,econpath,casepath))= Question1.initialSolution(spark, fullDF,resultpath = "results")


  def Q8_1(spark: SparkSession, fullDF: DataFrame=df(spark,econpath,casepath)): Unit = {
    import scala.reflect.io.Directory
    import java.io.File
      Question8.regionCorrelation(spark, fullDF)

  }

  def Q8_2(spark: SparkSession, fullDF: DataFrame=df(spark,econpath,casepath)): Unit = {
    Question8.regionFirstPeak(spark, fullDF, "results")
  }
}

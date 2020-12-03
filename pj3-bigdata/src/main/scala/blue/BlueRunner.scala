package blue


import org.apache.spark.sql.{DataFrame, SparkSession}

object BlueRunner  {

  val spark = SparkSession.builder()
    .master("local[4]")
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  //Type path here
  val casepath="path"
  val econpath="path"


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


  def Q8(spark: SparkSession, fullDF: DataFrame=df(spark,econpath,casepath)): Unit = {
    Question8.regionCorrelation(spark, fullDF)
    Question8.regionFirstPeak(spark, fullDF)
  }




}

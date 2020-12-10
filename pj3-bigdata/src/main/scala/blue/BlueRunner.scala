package blue


import org.apache.spark.sql.{AnalysisException, DataFrame, SparkSession}

object BlueRunner  {

  val spark = SparkSession.builder()
    .master("local[4]")
    .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  //Type path here


  val casepath3="s3a://adam-king-848/data/daily_stats.tsv"
  val econpath3="s3a://adam-king-848/data/economic_data_2018-2021.tsv"

  val econpath = econpath3
  val casepath = casepath3




//  def main(args: Array[String]): Unit = {
//
//    if (args.length <= 2) {
//      System.err.println("EXPECTED 2 ARGUMENTS: AWS Access Key, then AWS Secret Key, then S3 Bucket")
//      System.exit(1)
//    }
//    val accessKey = args(0)
//    val secretKey = args(1)
//    val filePath = args(2)
//
//    val spark = SparkSession.builder().appName("sample").getOrCreate()
//
//    spark.sparkContext.addJar("https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/2.7.4/hadoop-aws-2.7.4.jar")
//    spark.sparkContext.addJar("https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk/1.7.4/aws-java-sdk-1.7.4.jar")
//    spark.sparkContext.addJar("https://repo1.maven.org/maven2/org/apache/spark/spark-sql_2.12/3.0.0/spark-sql_2.12-3.0.0.jar")
//    spark.sparkContext.addJar("https://repo1.maven.org/maven2/org/scalanlp/breeze_2.13/1.1/breeze_2.13-1.1.jar")
//    spark.sparkContext.addJar("https://repo1.maven.org/maven2/org/scalanlp/breeze-natives_2.13/1.1/breeze-natives_2.13-1.1.jar")
//    spark.sparkContext.addJar("https://repo1.maven.org/maven2/org/scalanlp/breeze-viz_2.13/1.1/breeze-viz_2.13-1.1.jar")
//    spark.sparkContext.hadoopConfiguration.set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
//    spark.sparkContext.hadoopConfiguration.set("fs.s3a.access.key", accessKey)
//    spark.sparkContext.hadoopConfiguration.set("fs.s3a.secret.key", secretKey)
//    spark.sparkContext.hadoopConfiguration.set("fs.s3a.path.style.access", "true")
//    spark.sparkContext.hadoopConfiguration.set("fs.s3a.fast.upload", "true")
//    spark.sparkContext.hadoopConfiguration.set("fs.s3.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")
//    spark.sparkContext.hadoopConfiguration.set("fs.s3.awsAccessKeyId", accessKey)
//    spark.sparkContext.hadoopConfiguration.set("fs.s3.awsSecretAccessKey", secretKey)
//
//    println("Start")
//    Q1(spark)
//    Q8_1(spark)
//    Q8_2(spark)
//
//  }

  def df(spark: SparkSession, econpath:String,casepath:String): DataFrame  ={
    val regionDF = spark.read.json("s3a://adam-king-848/data/regionDict.json")

    val econRawDF = spark.read.option("delimiter","\t").option("header",true).csv(econpath)
    val caseRawDF = spark.read.option("delimiter","\t").option("header",true)
      .csv(casepath)
    val caseRegionDF = DataFrameManipulator.caseJoin(spark, regionDF, caseRawDF)
    val econRegionDF = DataFrameManipulator.econJoin(spark, regionDF, econRawDF)
    val fullDF = DataFrameManipulator.joinCaseEcon(spark, caseRegionDF, econRegionDF)
    fullDF
  }

  def Q1(spark:SparkSession, fullDF: DataFrame=df(spark,econpath,casepath)): Unit ={
    Question1.initialSolution(spark, fullDF, resultpath = "s3a://adam-king-848/results/blue/")

  }


  def Q8_1(spark: SparkSession, fullDF: DataFrame=df(spark,econpath,casepath)): Unit = {
    Question8.regionCorrelation(spark, fullDF)
  }

  def Q8_2(spark: SparkSession, fullDF: DataFrame=df(spark,econpath,casepath)): Unit = {
    Question8.regionFirstPeak(spark, fullDF, "s3a://adam-king-848/results/blue/")
  }

}

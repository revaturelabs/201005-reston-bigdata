package runner

import org.apache.spark.sql.SparkSession
import purple.Q1.HashtagsByRegion
import purple.Q2.HashtagsWithCovid

object Runner {
  def main(args: Array[String]): Unit = {
    val accessKey = args.apply(0)
    val secretKey = args.apply(1)
    val commandArgs = args.drop(2)

    val spark = SparkSession.builder().appName("covid-analysis").getOrCreate()

    spark.sparkContext.addJar("https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/2.7.4/hadoop-aws-2.7.4.jar")
    spark.sparkContext.addJar("https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk/1.7.4/aws-java-sdk-1.7.4.jar")
    spark.sparkContext.hadoopConfiguration.set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
    spark.sparkContext.hadoopConfiguration.set("fs.s3a.access.key", accessKey)
    spark.sparkContext.hadoopConfiguration.set("fs.s3a.secret.key", secretKey)
    spark.sparkContext.hadoopConfiguration.set("fs.s3a.path.style.access", "true")
    spark.sparkContext.hadoopConfiguration.set("fs.s3a.fast.upload", "true")
    spark.sparkContext.hadoopConfiguration.set("fs.s3.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")
    spark.sparkContext.hadoopConfiguration.set("fs.s3.awsAccessKeyId", accessKey)
    spark.sparkContext.hadoopConfiguration.set("fs.s3.awsSecretAccessKey", secretKey)

    // Command Line Interface format that accepts params
    // Have a use case for your question below
    commandArgs match {
      //==start purple team's questions==
      //PART A - save a csv file with the all of the regions combined as top 100 hashtags
      case Array("hbr") => HashtagsByRegion.getHashtagsByRegion(spark)
      //PART A - save a csv file with the region's top 100 hashtags (Include "QUOTES" around mutli-word region names)
      case Array("hbr", region) => HashtagsByRegion.getHashtagsByRegion(spark, region)
      //PART A - save a csv file for each available region and the combined regions
      case Array("hbrall") => HashtagsByRegion.getHashtagsByRegionAll(spark)
      //PART B - save a csv file with top 100 hashtags
      case Array("hwc") => HashtagsWithCovid.getHashtagsWithCovid(spark)
      //==end purple team's questions==
    }
  }
}

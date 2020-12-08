package orange

import org.apache.spark.sql.SparkSession

object testRunner {
  def main(args: Array[String]): Unit = {
    // START OF NEEDED CONFIGURATION
    if (args.length <= 2) {
      System.err.println("EXPECTED 2 ARGUMENTS: AWS Access Key, then AWS Secret Key, then S3 Bucket")
      System.exit(1)
    }
    val accessKey = args.apply(0)
    val secretKey = args.apply(1)
    val filePath = args.apply(2)

    val spark = SparkSession.builder().appName("orange-team-rules").getOrCreate()

    // The following is required in order for this to work properly
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

    // END OF NEEDED CONFIGURATION

    orangeRunner.borderAnalysis(spark)
  }

}

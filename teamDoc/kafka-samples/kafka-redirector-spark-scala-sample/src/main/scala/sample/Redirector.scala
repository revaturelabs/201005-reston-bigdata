package sample

import com.typesafe.config.{ConfigFactory, Config}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.streaming.Trigger

object Redirector {
  def main(args: Array[String]) {

    // aws-settings.conf is located in /src/main/resources/
    val config : Config = ConfigFactory.load("aws-settings.conf")
    val server1 = config.getString("aws.server1")
    val server2 = config.getString("aws.server2")
    val server3 = config.getString("aws.server3")

    val topicSource = config.getString("kafka.topicSource")
    val topicOutput = config.getString("kafka.topicOutput")
    val checkpointLocation = "C:\\tmp\\kafkaTest-consumer"

    val spark = SparkSession
      .builder
      .appName("kafka spark redirector")
      .master("local[*]")
      .getOrCreate()

    
    // https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html#continuous-processing
    
    spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", s"$server1, $server2, $server3")
      .option("subscribe", topicSource)
      .load()
      .selectExpr("CAST(key AS STRING)", "CAST(value AS STRING)")
      .writeStream
      .format("kafka")
      .option("checkpointLocation", checkpointLocation)
      .option("kafka.bootstrap.servers", s"$server1, $server2, $server3")
      .option("topic", topicOutput)
      .trigger(Trigger.Continuous("1 second"))  // only change in query
      .start()
      .awaitTermination()

  }
}

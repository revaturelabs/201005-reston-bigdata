package sample

import org.apache.log4j.{Level, LogManager}
import java.util.Properties
import com.typesafe.config.{Config, ConfigFactory}
import scala.collection.JavaConverters._
import java.time.Duration
import org.apache.kafka.clients.consumer.KafkaConsumer

// https://sparkbyexamples.com/kafka/apache-kafka-consumer-producer-in-scala/

object Consumer {
  def main(args: Array[String]) {

    val logger = LogManager.getRootLogger
    logger.setLevel(Level.WARN)

    val config : Config = ConfigFactory.load("aws-settings.conf")
    val server1 = config.getString("aws.server1")
    val server2 = config.getString("aws.server2")
    val server3 = config.getString("aws.server3")

    val topicSource = config.getString("kafka.topicSource")

    val props:Properties = new Properties()
    props.put("group.id", "rev-bigdata")
    props.put("bootstrap.servers",s"$server1, $server2, $server3")
    props.put("key.deserializer",
      "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer",
      "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("enable.auto.commit", "true")
    props.put("auto.commit.interval.ms", "1000")
    logger.info(s"============================= Starting Consumer =============================")
    val consumer = new KafkaConsumer(props)
    val topics = List(topicSource)
    try {
      consumer.subscribe(topics.asJava)
      logger.info(s"============================= Subscribing to $topics =============================")
      while (true) {
        val records = consumer.poll(Duration.ofMillis(100))
        for (record <- records.asScala) {
          println("Topic: " + record.topic() +
            ",Key: " + record.key() +
            ",Value: " + record.value() +
            ", Offset: " + record.offset() +
            ", Partition: " + record.partition())
        }
      }
    }catch{
      case e:Exception => e.printStackTrace()
    }finally {
    }
    consumer.close()
    logger.info("============================= Exiting =============================")
  }
}

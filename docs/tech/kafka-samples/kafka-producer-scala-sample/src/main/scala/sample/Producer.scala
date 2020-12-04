package sample

import org.apache.log4j.{Level, LogManager}
import java.util.Properties
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import com.typesafe.config.{ConfigFactory, Config}

// https://sparkbyexamples.com/kafka/apache-kafka-consumer-producer-in-scala/

object Producer {
  def main(args: Array[String]) {

    val logger = LogManager.getRootLogger
    logger.setLevel(Level.WARN)

    // aws-settings.conf is located in /src/main/resources/
    val config : Config = ConfigFactory.load("aws-settings.conf")
    val server1 = config.getString("aws.server1")
    val server2 = config.getString("aws.server2")
    val server3 = config.getString("aws.server3")
    val topic = config.getString("kafka.topicSource")

    val props:Properties = new Properties()
    props.put("bootstrap.servers",s"$server1, $server2, $server3")
    props.put("key.serializer",
      "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer",
      "org.apache.kafka.common.serialization.StringSerializer")
    props.put("acks","all")
    val producer = new KafkaProducer[String, String](props)

    val fileName = "https://raw.githubusercontent.com/apache/kafka/trunk/NOTICE"

    logger.info("===================( Starting loop )===================")

    var count = 1

    while(true) {
      val file = scala.io.Source.fromURL(fileName).mkString
      logger.info(s"===================( Retrieved file ${count} times )===================")
      val fileByLine = file.split("\n").filter(_ != "")
      try {
        for (line <- fileByLine) {
          val record = new ProducerRecord[String, String](topic, line)
          val metadata = producer.send(record)
          printf(s"sent record(key=%s value=%s) " +
            "meta(partition=%d, offset=%d)\n",
            record.key(), record.value(),
            metadata.get().partition(),
            metadata.get().offset())
        }
      }catch{
        case e:Exception => e.printStackTrace()
      }finally {
      }
      logger.info("===================( Going to sleep )===================")
      count += 1
      Thread.sleep(10000)
    }
    producer.close()
    logger.info("Exiting.")

  }
}

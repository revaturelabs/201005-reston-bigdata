package blue

import org.apache.spark.sql.{DataFrame, SparkSession}

import scala.collection.mutable.ArrayBuffer

object Question8 {

  def regionCorrelation(spark: SparkSession, df: DataFrame): Unit={
    import spark.implicits._

    var regionNames = ArrayBuffer[String]()
    df.select("name").distinct().rdd.map(region => regionNames += region.get(0).toString)
    for(region <- 0 to regionNames.length-1){

    }
  }

  def regionFirstPeak(spark: SparkSession, df: DataFrame): Unit= {

  }
}

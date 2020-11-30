package runner
import blue.Question8
import org.apache.spark.sql.SparkSession


object Runner {
  def main(args: Array[String]): Unit = {
    // Command Line Interface format that accepts params
    // Have a use case for your question below
    val spark = SparkSession.builder()
      .master("local[4]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    val df = spark.read.option("header",true).csv("example.csv")
    df.show()

    args match {
      case _ => Question8.regionCorrelation(spark, df)//println("Hello World")
    }
  }
}

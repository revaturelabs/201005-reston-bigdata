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

    args match {
      case _ => println("Hello World")
    }
  }
}


package runner
//import your package here like so:
import orange.orangeRunner

import org.apache.spark.sql.SparkSession


object Runner {
  def main(args: Array[String]): Unit = {
    // Command Line Interface format that accepts params
    //Have a use case for your question below
    val spark = SparkSession.builder()
      .master("local[4]")
      .getOrCreate()

    spark.sparkContext.setLogLevel("ERROR")

    args match {
      // Add the corresponding question function like so (Q8 is commented for rework):
      case Array(func) if(func == "Q2") => orangeRunner.borderAnalysis(spark)
      case _ => printMenu()
    }
  }

  def printMenu(): Unit ={
    // Add your input format in the help menu below:
    println("___________________________________Menu___________________________________")
    println("Q2 | Highest Discrepancies between bordering countries, highest infecction rate per capita among landlocked/water locked countries, highest infection per capita based on HDI")
  }
}


package runner
//import your package here like so:
import blue.Question8
import green.Q4.Question4
import green.Q5.Question5
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
      case Array(func,param1, param2) if(func == "Q8") => //Question8.regionCorrelation(spark, df)
      case Array(func,param1) if(func == "Q4") => Question4.PrintQuestion4(spark)
      case Array(func,param1) if(func == "Q5") => Question5.getMostDiscussion(spark)
      case _ => printMenu()
    }
  }

  def printMenu(): Unit ={
    // Add your input format in the help menu below:
    println("___________________________________Menu___________________________________")
    println("Q8 <Param 1> <Param 2> | GDP-Peak correlation and avg first peak")
    println("Q4 <Param 1> | Trend of COVID-19 discussion over twitter and correlation to spikes in age range 5-30")
    println("Q5 <Param 1> | COVID-19's peak discussion times")
  }
}


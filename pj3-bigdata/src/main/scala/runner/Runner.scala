
package runner
//import your package here like so:
import blue.Question8

import java.util.Scanner

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future, TimeoutException}
import scala.concurrent.duration.DurationInt
import scala.io.StdIn
import scala.concurrent.Future._
import scala.sys.exit

import org.apache.spark.sql.{DataFrame, SparkSession}
import green.Q4.Question4
import green.Q5.Question5
import org.apache.spark.sql.SparkSession

import purple.Q1.HashtagsByRegion
import purple.Q2.HashtagsWithCovid

object Runner {
   def main(args: Array[String]): Unit = {
    // Command Line Interface format that accepts params

    // Configs that William so kindly provided us with.
    if (args.length <= 2) {
      System.err.println("EXPECTED 3 ARGUMENTS: AWS Access Key, then AWS Secret Key, then S3 Bucket")
      System.exit(1)
//      case Array(func,param1) if(func == "Q4") => Question4.PrintQuestion4(spark)
//      case Array(func,param1) if(func == "Q5") => Question5.getMostDiscussion(spark)

    }
    val accessKey = args.apply(0)
    val secretKey = args.apply(1)
    val filePath = args.apply(2)

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
    // Thank you William


    println("Welcome to our Covid analysis. Here are the menu options:\n")
    userOption()

    // End of main()
  }
  
  // Declaring spark session at global scope
  val spark = SparkSession.builder().appName("Nahshon-test").getOrCreate()

  //loop condition for while loop manipulation.
  var loopagain = true

  def userOption():Unit =           {
    Menu.printMenu()
    println("\nPlease enter an option: ")

    // Timeout for the entire program. Set to ~20 minutes for presentation.
    // I used 30 sec or 5 min for testing.
    var timeout = 5.minutes.fromNow

    while (timeout.hasTimeLeft() || loopagain) {
      // Scanner for user input
      val uiScanner = new Scanner(System.in)
      // This is what we do only when time has run out.
      if (timeout.hasTimeLeft() == false) {
        println("\n[**SESSION TIMEOUT**]")
        // The exit statement ends the entire program. This point means timeout has
        // hit deadline without an exit statement, so we set the program to exit,
        // then ask the user if they would like to continue.
        loopagain = false
        // We establish a future thread to wait for users to choose to continue.
        val continueOption: Future[Unit] = Future{
          StdIn.readLine("The program will close in 1 minute. Would you like to continue?" +
            "\nType yes or no: ") match {
            case input if input.equalsIgnoreCase("yes") => {
              println("\nOkay, returning to main menu...\n")
              loopagain = true
              timeout = 5.minutes.fromNow
              Menu.printMenu()
              println("\nPlease enter an option: ")
            }
            case input if input.equalsIgnoreCase("no") => {
              println("\nThank you for your time!! Goodbye\n")
            }
            case _ => {
              println("\nWe'll take that as a no. Exiting program...")
            }
          }
        }

        // We call the above defined future here, but wait only 1 minute before the
        // final timeout and close program. If the future continues the program (by
        // setting loop again equal to true), then we set the timeout for 5 more
        // minutes, and head back up to the while loop.
        try {
          Await.ready(continueOption, 1.minute)
        } catch {
          case clockout: TimeoutException => println("\nShutting down...")
        }
      } else if (System.in.available() > 0) {
        // This is the code that executes while timeout still has time.
        uiScanner.next() match {

            case question if question.equalsIgnoreCase("Q1") => {
              println("\nWhich Regions handled COVID-19 the best assuming our metrics" +
                "\nare change in GDP by percentage and COVID-19 infection " +
                "\nrate per capita. (Jan 1 2020 - Oct 31 2020)\n")
              println("running code...")

              //        blue.BlueRunner.Q1(spark)
              println("Returning to main menu...")
              Menu.printMenu()
              println("\nPlease enter an option: ")
            }

            case question if question.equalsIgnoreCase("Q2") => {
              println("\nFind the top 5 pairs of countries that share a land border and have the" +
                "\nhighest discrepancy in covid-19 infection rate per capita. " +
                "\nAdditionally, find the top 5 landlocked countries that have the " +
                "\nhighest discrepancy in covid-19 infection rate per capita.\n")
              println("running code...")
              //        color.Question2.runnerfunc(spark)
              println("Returning to main menu...")
              Menu.printMenu()
              println("\nPlease enter an option: ")
            }

            case question if question.equalsIgnoreCase("Q3") => {
              println("\nLive update by Region of current relevant totals from COVID-19 data.\n")
              println("running code...")
              //        color.Question3.runnerfunc(spark)
              println("Returning to main menu...")
              Menu.printMenu()
              println("\nPlease enter an option: ")
            }

            case question if question.equalsIgnoreCase("Q4") => {
              println("\nIs the trend of the global COVID-19 discussion going up or down?" +
                "\nDo spikes in infection rates of the 5-30 age range affect the volume of discussion?\n")
              println("running code...")
              //        color.Question4.runnerfunc(spark)
              println("Returning to main menu...")
              Menu.printMenu()
              println("\nPlease enter an option: ")
            }

            case question if question.equalsIgnoreCase("Q5") => {
              println("\nWhen was COVID-19 being discussed the most?\n")
              println("running code...")
              //        color.Question5.runnerfunc(spark)
              println("Returning to main menu...")
              Menu.printMenu()
              println("\nPlease enter an option: ")
            }

            case question if question.equalsIgnoreCase("Q6") => {
              println("\nWhat percentage of countries have an increasing COVID-19 Infection rate?\n")
              println("running code...")
              //        color.Question6.runnerfunc(spark)
              println("Returning to main menu...")
              Menu.printMenu()
              println("\nPlease enter an option: ")
            }

            case question if question.equalsIgnoreCase("Q7") => {
              println("\nWhat are the hashtags used to describe COVID-19 by Region (e.g. #covid, " +
                "\n#COVID-19, #Coronavirus, #NovelCoronavirus)? Additionally, what are the top 10 " +
                "\ncommonly used hashtags used alongside COVID hashtags?\n")
              println("running code...")
              //        color.Question7.runnerfunc(spark)
              println("Returning to main menu...")
              Menu.printMenu()
              println("\nPlease enter an option: ")
            }

            case question if question.equalsIgnoreCase("Q8") => {
              println("\nIs there a significant relationship between a Region’s cumulative" +
                "\nGDP and Infection Rate per capita? What is the average amount of time" +
                "\nit took for each region to reach its first peak in infection rate per capita?\n")
              println("running code...")
              //  blue.BlueRunner.Q8_1(spark)
              //  blue.BlueRunner.Q8_2(spark)
              println("Returning to main menu...")
              Menu.printMenu()
              println("\nPlease enter an option: ")
            }

            case leave if leave.equalsIgnoreCase("Exit") => {
              println("\nThank you for your time! Goodbye :)\n")
              timeout = timeout - timeout.timeLeft
              loopagain = false
              exit
            }

            case somethingelse => {
              println(s"\n'${somethingelse}' is not a recognized option. Maybe you should try again.\n")
              Menu.printMenu()
              println("\nPlease enter an option: ")
            }
          }
      }

    }

  }

}


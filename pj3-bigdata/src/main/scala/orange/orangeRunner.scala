package orange

import org.apache.spark
import org.apache.spark.SparkConf
import org.apache.spark.sql.functions.{asc, desc, round, max}
import org.apache.spark.sql.{DataFrame, SparkSession, functions}


object orangeRunner {
  def borderAnalysis(spark: SparkSession){
//  def main(args: Array[String]): Unit={ //comment and uncomment based on if using runner.Runner or not.
//  val appName = "Orange"
//  val spark = SparkSession.builder()
//    .appName(appName)
//    .master("local[4]")
//    .getOrCreate()
    import spark.implicits._
    spark.sparkContext.setLogLevel("WARN")

    //import the covid data from testData file as well as the border data from the provided border data
    //val country_stats = spark.read.option("multiline", "true").option("header", "true").option("sep", "\t").csv("countries_general_stats.tsv")
    val country_stats = spark.read.option("multiline", "true").option("header", "true").option("sep", "\t").csv("s3a://adam-king-848/data/countries_general_stats.tsv")
   //val country_data = spark.read.option("multiline", "true").option("header", "true").option("sep", "\t").csv("daily_stats.tsv")
      val country_data = spark.read.option("multiline", "true").option("header", "true").option("sep", "\t").csv("s3a://adam-king-848/data/owid_daily_stats.tsv")

    val country_pop = country_stats.select($"COUNTRY", $"POPULATION".cast("Int"))
    val country_cases = country_data
      .filter($"TOTAL_CASES" =!= "NULL")
      .withColumn("Cases", $"TOTAL_CASES".cast("Int"))
      .select($"location".as("COUNTRY"), $"Cases".as("Total Cases"))
      .groupBy($"COUNTRY")
      .agg(max($"Total Cases").as("TOTAL CASES"))
      .sort(asc("COUNTRY"))


    val borders = joinCodesAndBorders(spark)

    //create "infection_rate" from covid data directory with daily covid data
    val infection_rate = country_cases
      .join(country_pop, country_cases("COUNTRY") === country_pop("COUNTRY"))
      .select(country_cases("COUNTRY"), $"TOTAL CASES", $"POPULATION",
        ($"TOTAL CASES" * 100 / $"POPULATION").as("infection_rate_per_capita"))
      .sort(desc("infection_rate_per_capita"))
      .withColumn("infection_rate_per_capita",'infection_rate_per_capita.cast("Decimal(5,3)"))


    /*
  bcountries have land borders
     */
    val bcountries = borders.join(infection_rate, borders("country_name") === infection_rate("COUNTRY"), "right")
      .select(infection_rate("COUNTRY").as("country_name"),  borders("border_country"),
        infection_rate("infection_rate_per_capita").as("country_infection_rate"))

    /*
    the result of joining our border countries with our infection data. This gives us information about the home country, its infection rate,
    bordering countries and their infection rate, and a delta value, which is the difference between these 2 rates.
     */
    val res1 = bcountries.join(infection_rate, bcountries("border_country") === infection_rate("COUNTRY"), "inner")
      .select(bcountries("country_name"), bcountries("country_infection_rate"), bcountries("border_country"),
        infection_rate("infection_rate_per_capita").as("country_border_infection_rate_per_capita"),
        (bcountries("country_infection_rate") - infection_rate("infection_rate_per_capita")).as("delta"))


    /*
    Part 2 of question is asking about land and water locked countries, use dataframes defined below to create dataframes with land/water locked countries
    and combine them
     */
    val landLocked = dictionaries.landLocked.toDF("country_name")

    val waterLocked = createWaterLocked(bcountries, spark)

    //combine landLocked dataframe with infection_rate to make a dataframe with the infection rate of land locked countries
    val landLockedInfRate = landLocked.join(infection_rate, landLocked("country_name") === infection_rate("COUNTRY"), "inner")
      .select(landLocked("country_name"),
        infection_rate("infection_rate_per_capita").as("infection_rate_per_capita(%)"))

    //combine waterLocked dataframe with infection_rate to make a dataframe with the infection rate of water locked countries
    val waterLockedInfRate = waterLocked.join(infection_rate, waterLocked("country_name") === infection_rate("COUNTRY"), "inner")
      .select(waterLocked("country_name"),
        infection_rate("infection_rate_per_capita").as("infection_rate_per_capita(%)"))

       //creates a dataframe using the development rankings dictionary with two columns, ranking (first, second, third)
       //  and country_name (the name of the country)
       val rankings = dictionaries.developmentRankings.toSeq.toDF("ranking", "country")
         .select($"ranking", functions.explode($"country").as("country_name"))

    //joins the rankings dataframe with the infection_rate dataframe on the country name field in both dataframes
    val rankingsWithRate = rankings.join(infection_rate, rankings("country_name") === infection_rate("COUNTRY"), "inner")



    //RESULT QUERIES

    /*
    query to give result to part 1. by using delta > 0, we can ensure no duplicates of countries in opposite directions, such as
    having both Israel-Lebanon and Lebanon-Israel.
     */
    println("Largest Difference(Delta) in Bordering Countries")
    res1.select("*")
      .where($"delta" > 0)
      .orderBy(desc("delta")) //using desc here allows us to get the largest differences at the top, and smaller differences at the end
      .show(5)

    /* Queries to give us the answer to the second part of our question. Using the Dataframes for land and water locked countries, we can do simple
    queries to give the required answers
     */
    println("Highest Infection Rate in Land Locked Countries")
    landLockedInfRate.select("*")
      .orderBy(desc("infection_rate_per_capita(%)"))
      .show(5)

    println("Highest Infection Rate in Water Locked Countries")
    waterLockedInfRate.select("*")
      .orderBy(desc("infection_rate_per_capita(%)"))
      .show(5)

    /*
    query to find the countries with the highest infection rate per capita in each development category,
    i.e. First, Second, or Third.
    Shows the country name, the country's development, and the infection rate per capita for the country.
 */
    println("Highest infection rate with the highest ranking countries by HDI (Human Development Index)")
    rankingsWithRate.select("country_name", "infection_rate_per_capita")
      .where(rankingsWithRate("ranking") === "First")
      .orderBy(desc("infection_rate_per_capita"))
      .show(5)

    println("Highest infection rate with the average ranking countries by HDI (Human Development Index)")
    rankingsWithRate.select("country_name", "infection_rate_per_capita")
      .where(rankingsWithRate("ranking") === "Second")
      .orderBy(desc("infection_rate_per_capita"))
      .show(5)

    println("Highest infection rate with the lowest ranking countries by HDI (Human Development Index)")
    rankingsWithRate.select("country_name", "infection_rate_per_capita")
      .where(rankingsWithRate("ranking") === "Third")
      .orderBy(desc("infection_rate_per_capita"))
      .show(5)

  }



/**
 * Uses the dataframe of our border countries and looks for there to be a NULL for border country. This means
 * there is no land border, meaning the country is waterlocked.
 *
 * @param infectionFrame is the dataframe that has countries, their infection rate and a country they border
 * @return dataframe that has all countries with no countries bordering them by land
 */
def createWaterLocked(infectionFrame: DataFrame , spark:SparkSession): DataFrame ={
    import spark.implicits._
    val waterLocked = infectionFrame.filter($"border_country".isNull)
      .select($"country_name")
    waterLocked
  }

  /**
   * Takes the border dictionary and the country code dictionary from the dictionaries class and creates
   *  dataframes using both of them.  Those dataframes are then joined to create a dataframe with the
   *  following format:
   *  country_name, country_code, border_country, and country_border_code
   *
   *
   * @param spark is the spark session that is used in the method
   * @return is the final dataframe that is the combination of the two dictionaries.
   */
  def joinCodesAndBorders(spark: SparkSession) = {

    import spark.implicits._

    spark.sparkContext.setLogLevel("WARN")

    //creates a dataframe from the border dictionary in the dictionaries class
    val borders = dictionaries.borders.toSeq.toDF("country", "borders")
      .select($"country", functions.explode($"borders").as("border"))

    //creates a dataframe from the country code dictionary in the dictionaries class
    val codes = dictionaries.countryCodes.toSeq.toDF("code", "country")

    //joins the border dictionary with the country code dictionary on the name of the key in the borders dictionary
    // to create a dataframe that contains the following format:
    //  country_name, country_code, border_country

    val first = borders.join(codes, borders("country") === codes("country"), "inner")
      .select(borders("country").as("country_name"), codes("code").as("country_code"),
        borders("border").as("border_country"))


    //joins the dataframe that was just created to the country code dictionary on the value in the country code to
    //  create a dataframe that contains the following format:
    //  country_name, country_code, border_country, border_country_code
    val second = first.join(codes, first("border_country") === codes("country"), "inner")
      .select(first("country_name"), first("country_code"), first("border_country"),
        codes("code").as("border_country_code"))

    //returns the final dataframe so it can be used in other queries in the application
    second
  }

}

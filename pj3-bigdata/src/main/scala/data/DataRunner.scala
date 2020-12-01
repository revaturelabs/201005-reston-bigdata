package data_parsing

import org.apache.spark.sql.types.{DoubleType, IntegerType, StructType}
import org.apache.spark.sql.{Column, DataFrame, Dataset, SparkSession, functions}

object DataRunner {
 /* def main(args: Array[String]) ={
    val spark: SparkSession = SparkSession.builder()
      .appName("Data Initializer")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._
    spark.sparkContext.setLogLevel("ERROR")

    val countryMetaDataPath: String = "general_country_stats.tsv"
    val caseDataPath: String = ""
    val twitterDataPath: String = ""

    val saveLocation: String = ""

    val countriesSchema =

    val countriesDS: Dataset[Country] = spark.read
                                        .option("delimiter" -> "\t")
                                        .csv(countryMetaDataPath)
                                        .as[Country]

    var caseDataDS: Dataset[cData] = spark.read
                                     .option("delimiter" -> "\t")
                                     .csv(caseDataPath)
                                     .as[cData]

    var twitterDataDS: Dataset[Tweet] = spark.read
                                        .option("delimiter" -> "\t")
                                        .csv(twitterDataPath)
                                        .as[Tweet]

    for(region <- regions_dictionary) {

    }

  }

  def initialize(countriesDS: Dataset[Country]): Unit = {
    val countries = regionCountries(name)
    val regionDS = countriesDS.filter(isInRegion($"name")).as[Region]

  }

  def fullUpdate(): Unit = {
    updateCases()
    updateDeaths()
    updateRecoveries()
  }

  def getCountries(name: String): Dataset = {
    countriesDF.select("name" == name).as[Country]
  }

  def updateCases(): Unit = {
    var agg_cases = 0
    for(country <- countries)
      agg_cases += getCountry(country).total_cases
  }

  def updateDeaths(): Unit = {
    var agg_deaths = 0
    for (country <- countries)
      agg_deaths += getCountry(country).deaths
  }

  def updateRecoveries(): Unit = {
    var agg_recoveries = 0
    for (country <- countries)
      agg_recoveries = getCountry(country).recoveries
  }

  def isInRegion(name: String): Boolean = {
    if (countries.contains(name))
      true
    else
      false
  }*/
}

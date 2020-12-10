package blue

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{explode, when}

object DataFrameManipulator {
  def caseJoin(spark: SparkSession, regionDF: DataFrame, caseDF: DataFrame): DataFrame ={
    import spark.implicits._

    val regionDict = regionDF
      .select($"name", explode($"countries") as "country2")

    caseDF
      .select( $"date", $"country", $"total_cases", $"total_cases_per_million", $"new_cases", $"new_cases_per_million")
      .join(regionDict, $"country" === $"country2")
      .drop($"country2")
      .withColumn("new_cases", when($"new_cases"==="NULL", 0).otherwise($"new_cases"))
      .withColumn("total_cases", when($"total_cases"==="NULL", 0).otherwise($"total_cases"))
      .withColumn("new_cases_per_million", when($"new_cases_per_million"==="NULL", 0).otherwise($"new_cases_per_million"))
      .withColumn("total_cases_per_million", when($"total_cases_per_million"==="NULL", 0).otherwise($"total_cases_per_million"))
      .filter($"date" =!= "null")
      .sort($"date" desc_nulls_first)
  }

   def econJoin(spark: SparkSession, regionDF: DataFrame, econDF: DataFrame): DataFrame ={
    import spark.implicits._

    val regionDict = regionDF
      .select($"name", explode($"countries") as "country")
      .select($"name" as "region", $"country" as "country2")

     econDF
       .join(regionDict, $"name" === $"country2")
       .select($"year",$"region",$"name" as "country",$"gdp_currentPrices" as "current_prices_gdp" ,$"gdp_perCap_currentPrices" as "gdp_per_capita")
       .drop($"country2")
   }


  def joinCaseEcon(spark: SparkSession, caseDF: DataFrame, econDF: DataFrame): DataFrame = {
    import spark.implicits._
    econDF.createOrReplaceTempView("econDFTemp")
    caseDF.createOrReplaceTempView("caseDFTemp")
    val caseEconDF = spark.sql(
      "SELECT e.year, e.region, c.country, e.current_prices_gdp, e.gdp_per_capita, c.total_cases, c.new_cases, c.new_cases_per_million, c.total_cases_per_million,c.date " +
        " FROM econDFTemp e JOIN caseDFTemp c " +
        "ON e.country == c.country " +
        "ORDER BY region, gdp_per_capita")

    caseEconDF
  }
}

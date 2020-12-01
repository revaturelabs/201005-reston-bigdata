package blue

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.explode

object DataFrameManipulator {
  def caseJoin(spark: SparkSession, regionDF: DataFrame, caseDF: DataFrame): DataFrame ={
    import spark.implicits._

    val regionDict = regionDF
      .select($"name", explode($"countries") as "country")
      .select($"name", $"agg_population", $"country.name" as "country")

    caseDF
      .select( $"date", $"country", $"total_cases", $"new_cases")
      .join(regionDict, $"country")

  }

   def econJoin(spark: SparkSession, regionDF: DataFrame, econDF: DataFrame): DataFrame ={
    import spark.implicits._

    val regionDict = regionDF
      .select($"name", explode($"countries") as "country")
      .select($"name" as "region", $"agg_population", $"country.name" as "country")



    econDF
      .select($"year",$"region", $"country", $"GDP")
      .join(regionDict, $"country")

  }
  

  def joinCaseEcon(spark: SparkSession, caseDF: DataFrame, econDF: DataFrame): DataFrame = {
    econDF.createOrReplaceTempView("econDFTemp")
    caseDF.createOrReplaceTempView("caseDFTemp")
    val caseEconDF = spark.sql(
      "SELECT c.region, c.country, e.gdp, c.new_cases" +
        " FROM econDFTemp e JOIN caseDFTemp c" +
        "ON e.country = c.country" +
        "ORDER BY region, gdp")
    caseEconDF
  }
}

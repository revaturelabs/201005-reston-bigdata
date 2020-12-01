package blue

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.explode

object DataFrameManipulator {
  def caseJoin(spark: SparkSession, regionDF: DataFrame, caseDF: DataFrame): DataFrame ={
    import spark.implicits._

    val regionDict = regionDF
      .select($"name", explode($"countries") as "country")
      .select($"name" as "region", $"agg_population", $"country.name" as "country")

    caseDF
      .select( $"date", $"country", $"total_cases", $"new_cases")
      .join(regionDict, $"country")

  }

  def econData(spark: SparkSession, regionDF: DataFrame, caseDF: DataFrame): DataFrame ={}


}

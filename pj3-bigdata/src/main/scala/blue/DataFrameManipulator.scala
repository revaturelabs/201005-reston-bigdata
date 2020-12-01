package blue

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.explode

object DataFrameManipulator {
  def caseJoin(spark: SparkSession, regionDF: DataFrame, caseDF: DataFrame): DataFrame ={
//    import spark.implicits._
//    val regionDict = regionDF
//      .select($"name", explode($"countries") as "country")
//      .select($"name" as "region", $"country.name" as "country")
//
//    val data = caseDF
//      .select($"name", $"country"$"total_cases", $"new_cases")


  }

  def econData(spark: SparkSession, regionDF: DataFrame, caseDF: DataFrame): DataFrame ={}


}

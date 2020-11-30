package scratch

import org.apache.spark.sql.catalyst.analysis.UnresolvedAttribute
import org.apache.spark.sql.types.{ArrayType, DataType, MapType, StructField, StructType}
import org.apache.spark.sql.{SparkSession, functions}

object StackRunner {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("Spark SQL")
      .master("local[4]")
      .getOrCreate()
    spark.sparkContext.setLogLevel("ERROR")

    import spark.implicits._

    var nodatadf = spark.read.option("multiline", "true").json("Laos.json").drop($"data")
    var datadf = spark.read.option("multiline", "true").json("Laos.json").select($"data")
    datadf.show();

    //var df = spark.read.option("multiline", "true").json("Laos.json")
    var df = spark.read.option("multiline", "true").json("owid-covid-data.json")
    df.show(1)
    /*
    val rotatedf = df.select(functions.expr(s"stack(${df.columns.length}, ${df.columns.mkString(",")})"))
    val cols = explodeNestedFieldNames(rotatedf.schema)
    val newdf = rotatedf.select(cols.head, cols.tail : _*).drop($"col0")
    newdf.show();
    val newdf2 = newdf.select( functions.explode(functions.arrays_zip(newdf.columns.map(newdf(_)):_*)))
    val colls2= explodeNestedFieldNames(newdf2.schema)
    println(colls2);
    newdf2.select(colls2.head, colls2.tail: _*).show(10);
     */


  }

  /*
  This function comes from DataBricks and is under their copyright + under Apache 2.0 license
  The code was taken from
  https://github.com/delta-io/delta/blob/f72bb4147c3555b9a0f571b35ac4d9a41590f90f/src/main/scala/org/apache/spark/sql/delta/schema/SchemaUtils.scala#L123
   */
  def explodeNestedFieldNames(schema: StructType): Seq[String] = {
    def explode(schema: StructType): Seq[Seq[String]] = {
      def recurseIntoComplexTypes(complexType: DataType): Seq[Seq[String]] = {
        complexType match {
          case s: StructType => explode(s)
          case a: ArrayType => recurseIntoComplexTypes(a.elementType)
          case m: MapType =>
            recurseIntoComplexTypes(m.keyType).map(Seq("key") ++ _) ++
              recurseIntoComplexTypes(m.valueType).map(Seq("value") ++ _)
          case _ => Nil
        }
      }

      schema.flatMap {
        case StructField(name, s: StructType, _, _) =>
          Seq(Seq(name)) ++ explode(s).map(nested => Seq(name) ++ nested)
        case StructField(name, a: ArrayType, _, _) =>
          Seq(Seq(name)) ++ recurseIntoComplexTypes(a).map(nested => Seq(name) ++ nested)
        case StructField(name, m: MapType, _, _) =>
          Seq(Seq(name)) ++ recurseIntoComplexTypes(m).map(nested => Seq(name) ++ nested)
        case f => Seq(f.name) :: Nil
      }
    }

    explode(schema).map(UnresolvedAttribute.apply(_).name)
  }
}

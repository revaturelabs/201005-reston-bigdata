name := "kafka-redirector-spark-scala-sample"

version := "1.0"

scalaVersion := "2.12.10"

libraryDependencies += "com.typesafe" % "config" % "1.4.1"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.0.0"
libraryDependencies += "org.apache.spark" %% "spark-sql-kafka-0-10" % "3.0.0"
libraryDependencies += "org.apache.spark" %% "spark-streaming" % "3.0.0"
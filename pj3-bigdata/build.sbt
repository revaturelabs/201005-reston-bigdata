name := "201005-reston-bigdata"

version := "0.1"

libraryDependencies  ++= Seq(
  // other dependencies here
  "org.scalanlp" %% "breeze" % "1.1",
  // native libraries are not included by default. add this if you want them (as of 0.7)
  // native libraries greatly improve performance, but increase jar sizes.
  // It also packages various blas implementations, which have licenses that may or may not
  // be compatible with the Apache License. No GPL code, as best I know.
  "org.scalanlp" %% "breeze-natives" % "1.1",
  // the visualization library is distributed separately as well.
  // It depends on LGPL code.
  "org.scalanlp" %% "breeze-viz" % "1.1",
  "org.apache.spark" %% "spark-sql" % "3.0.1",
  "org.apache.bahir" %% "spark-streaming-twitter" % "2.4.0",
  "org.apache.httpcomponents" % "httpclient" % "4.5.12",
  "commons-io" % "commons-io" % "2.8.0",
  "org.jsoup" % "jsoup" % "1.13.1"

)

scalaVersion := "2.12.10"

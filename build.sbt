scalaVersion := "2.12.10"

ThisBuild / scalaVersion  := "2.12.10"
ThisBuild / version       := "1.0"
ThisBuild / libraryDependencies ++= commonDependencies
ThisBuild / organization := "com.revature"

// Projects
lazy val root = project
  .in(file("."))
  .settings(
    name := "201005-reston-bigdata"
  )
  .aggregate(
    teamBlue,
    teamCovidLiveUpdateApp,
    teamData,
    teamGreen,
    teamOrange,
    teamPurple,
    teamRed
  )

// Sub-Projects
// hint: intellij alt-j provides sublime text style multiple selections highlight a selection and hit alt-j to add the next occurrence
lazy val teamBlue = project.settings(name := "teamBlue")
lazy val teamCovidLiveUpdateApp = project.settings(name := "teamCovidLiveUpdateApp")
lazy val teamData = project.settings(name := "teamData")
lazy val teamGreen = project.settings(name := "teamGreen")
lazy val teamOrange = project.settings(name := "teamOrange")
lazy val teamPurple = project.settings(name := "teamPurple")
lazy val teamRed = project.settings(name := "teamRed")

// Settings
lazy val settings =
  commonSettings ++
    wartremoverSettings

lazy val commonSettings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    // release: point-in-time releases.  considered solid, stable and perpetual in order
    Resolver.sonatypeRepo("releases"),
    // snapshots capture a work in progress and are used during development
    Resolver.sonatypeRepo("snapshots")
  )
)

lazy val compilerOptions = Seq(
  // enable add'l warnings where generated code depends on assumptions
  "-unchecked",
  // emit warning and location for usages that should be imported explicitly
  "-feature",
  // Existential types (besides wildcard types) can be written and inferred
  "-language:existentials",
  // Allow higher-kinded types
  "-language:higherKinds",
  // Allow definition of implicit functions called views
  "-language:implicitConversions",
  // Allow post fix operator notation, not recommended
  "-language:postfixOps",
  // Emit warnings and locations for usages of deprecated APIs
  "-deprecation",
  // Specify character encoding
  "-encoding",
  "utf8"
)

lazy val wartremoverSettings = Seq(
  // turn on all warts (except Throw), only for compilation
  wartremoverWarnings in (Compile, compile) ++= Warts.allBut(Wart.Throw)
)

// Dependencies
// Two dependencies below resolves java.lang.NoClassDefFoundError on Google DataProc
// https://github.com/googleapis/java-logging/issues/276

lazy val dependencies =
  new {
    val sparkSqlV                   = "3.0.0"
    val breezeV                     = "1.1"
    val breezeVizV                  = "1.1"
    val breezeNativesV              = "1.1"
    val sparkStreamingTwitterV      = "2.4.0"
    val httpclientV                 = "4.5.12"
    val commonsIoV                  = "2.8.0"
    val jsoupV                      = "1.13.1"

    val sparkSql                  = "org.apache.spark"            %% "spark-sql"                  % sparkSqlV
    val breeze                    = "org.scalanlp"                %% "breeze"                     % breezeV
    val breezeViz                 = "org.scalanlp"                %% "breeze-viz"                 % breezeVizV
    val breezeNatives             = "org.scalanlp"                %% "breeze-natives"             % breezeNativesV
    val sparkStreamingTwitter     = "org.apache.bahir"            %% "spark-streaming-twitter"    % sparkStreamingTwitterV
    val httpclient                = "org.apache.httpcomponents"    % "httpclient"                 % httpclientV
    val commonsIo                 = "commons-io"                   % "commons-io"                 % commonsIoV
    val jsoup                     = "org.jsoup"                    % "jsoup"                      % jsoupV
  }

lazy val commonDependencies = Seq(
  dependencies.sparkSql,
  dependencies.breeze,
  dependencies.breezeViz,
  dependencies.breezeNatives,
  dependencies.sparkStreamingTwitter,
  dependencies.httpclient,
  dependencies.commonsIo,
  dependencies.jsoup
)

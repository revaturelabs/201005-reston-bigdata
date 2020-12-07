import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._


object CovidLiveUpdates {
    def main(args: Array[String]): Unit = {

        //Spark Session
        if (args.length <= 2) {
            System.err.println("EXPECTED 2 ARGUMENTS: AWS Access Key, then AWS Secret Key, then S3 Bucket")
            System.exit(1)
        }
        val accessKey = args.apply(0)
        val secretKey = args.apply(1)

        val spark = SparkSession.builder()
            .appName("CovidLiveUpdates")
            .master("local[4]")
            .getOrCreate()

        // The following is required in order for this to work properly
        spark.sparkContext.addJar("https://repo1.maven.org/maven2/org/apache/hadoop/hadoop-aws/2.7.4/hadoop-aws-2.7.4.jar")
        spark.sparkContext.addJar("https://repo1.maven.org/maven2/com/amazonaws/aws-java-sdk/1.7.4/aws-java-sdk-1.7.4.jar")
        spark.sparkContext.hadoopConfiguration.set("fs.s3a.impl", "org.apache.hadoop.fs.s3a.S3AFileSystem")
        spark.sparkContext.hadoopConfiguration.set("fs.s3a.access.key", accessKey)
        spark.sparkContext.hadoopConfiguration.set("fs.s3a.secret.key", secretKey)
        spark.sparkContext.hadoopConfiguration.set("fs.s3a.path.style.access", "true")
        spark.sparkContext.hadoopConfiguration.set("fs.s3a.fast.upload", "true")
        spark.sparkContext.hadoopConfiguration.set("fs.s3.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")
        spark.sparkContext.hadoopConfiguration.set("fs.s3.awsAccessKeyId", accessKey)
        spark.sparkContext.hadoopConfiguration.set("fs.s3.awsSecretAccessKey", secretKey)
        //Set log level for sbt shell
        spark.sparkContext.setLogLevel("ERROR")


        //Processes data and performs union to create single regions DataFrame
        def dataProcessing = {
            //Calls DataUpdate to provide up to date stats for all regions

            //Creates a DataFrame from JSON for each region
            val africaTemp = spark.read.json("s3://adam-king-848/data/africa.json")
            val asiaTemp = spark.read.json("s3://adam-king-848/data/asia.json")
            val caribbeanTemp = spark.read.json("s3://adam-king-848/data/caribbean.json")
            val centralAmericaTemp = spark.read.json("s3://adam-king-848/data/central_america.json")
            val europeTemp = spark.read.json("s3://adam-king-848/data/europe.json")
            val northAmericaTemp = spark.read.json("s3://adam-king-848/data/north_america.json")
            val oceaniaTemp = spark.read.json("s3://adam-king-848/data/oceania.json")
            val southAmericaTemp = spark.read.json("s3://adam-king-848/data/south_america.json")

            //Spark SQL to select proper columns from each DF and save as new DF

            //Creates final Africa DF
            val africa = africaTemp.select(
                lit("Africa").as("Region"),
                sum("cases") as "Total Cases",
                sum("todayCases") as "Today's Cases",
                bround(sum("todayCases") / sum("cases") * 100, 2) as "Cases Percent Change",
                sum("deaths") as "Total Deaths",
                sum("todayDeaths") as "Today's Deaths",
                bround(sum("todayDeaths") / sum("deaths") * 100, 2) as "Death Percent Change",
                sum("recovered") as "Total Recoveries",
                sum("todayRecovered") as "Today's Recoveries",
                bround(sum("todayRecovered") / sum("recovered") * 100, 2) as "Recoveries Percent Change")

            //Creates final Asia DF
            val asia = asiaTemp.select(
                lit("Asia").as("Region"),
                sum("cases") as "Total Cases",
                sum("todayCases") as "Today's Cases",
                bround(sum("todayCases") / sum("cases") * 100, 2) as "Cases Percent Change",
                sum("deaths") as "Total Deaths",
                sum("todayDeaths") as "Today's Deaths",
                bround(sum("todayDeaths") / sum("deaths") * 100, 2) as "Death Percent Change",
                sum("recovered") as "Total Recoveries",
                sum("todayRecovered") as "Today's Recoveries",
                bround(sum("todayRecovered") / sum("recovered") * 100, 2) as "Recoveries Percent Change")

            //Creates final Carribean DF
            val caribbean = caribbeanTemp.select(
                lit("Caribbean").as("Region"),
                sum("cases") as "Total Cases",
                sum("todayCases") as "Today's Cases",
                bround(sum("todayCases") / sum("cases") * 100, 2) as "Cases Percent Change",
                sum("deaths") as "Total Deaths",
                sum("todayDeaths") as "Today's Deaths",
                bround(sum("todayDeaths") / sum("deaths") * 100, 2) as "Death Percent Change",
                sum("recovered") as "Total Recoveries",
                sum("todayRecovered") as "Today's Recoveries",
                bround(sum("todayRecovered") / sum("recovered") * 100, 2) as "Recoveries Percent Change")

            //Creates final Central America DF
            val centralAmerica = centralAmericaTemp.select(
                lit("Central America").as("Region"),
                sum("cases") as "Total Cases",
                sum("todayCases") as "Today's Cases",
                bround(sum("todayCases") / sum("cases") * 100, 2) as "Cases Percent Change",
                sum("deaths") as "Total Deaths",
                sum("todayDeaths") as "Today's Deaths",
                bround(sum("todayDeaths") / sum("deaths") * 100, 2) as "Death Percent Change",
                sum("recovered") as "Total Recoveries",
                sum("todayRecovered") as "Today's Recoveries",
                bround(sum("todayRecovered") / sum("recovered") * 100, 2) as "Recoveries Percent Change")

            //Creates final Europe DF
            val europe = europeTemp.select(
                lit("Europe").as("Region"),
                sum("cases") as "Total Cases",
                sum("todayCases") as "Today's Cases",
                bround(sum("todayCases") / sum("cases") * 100, 2) as "Cases Percent Change",
                sum("deaths") as "Total Deaths",
                sum("todayDeaths") as "Today's Deaths",
                bround(sum("todayDeaths") / sum("deaths") * 100, 2) as "Death Percent Change",
                sum("recovered") as "Total Recoveries",
                sum("todayRecovered") as "Today's Recoveries",
                bround(sum("todayRecovered") / sum("recovered") * 100, 2) as "Recoveries Percent Change")

            //Creates final North America DF
            val northAmerica = northAmericaTemp.select(
                lit("North America").as("Region"),
                sum("cases") as "Total Cases",
                sum("todayCases") as "Today's Cases",
                bround(sum("todayCases") / sum("cases") * 100, 2) as "Cases Percent Change",
                sum("deaths") as "Total Deaths",
                sum("todayDeaths") as "Today's Deaths",
                bround(sum("todayDeaths") / sum("deaths") * 100, 2) as "Death Percent Change",
                sum("recovered") as "Total Recoveries",
                sum("todayRecovered") as "Today's Recoveries",
                bround(sum("todayRecovered") / sum("recovered") * 100, 2) as "Recoveries Percent Change")

            //Creates final Oceania DF
            val oceania = oceaniaTemp.select(
                lit("Oceania").as("Region"),
                sum("cases") as "Total Cases",
                sum("todayCases") as "Today's Cases",
                bround(sum("todayCases") / sum("cases") * 100, 2) as "Cases Percent Change",
                sum("deaths") as "Total Deaths",
                sum("todayDeaths") as "Today's Deaths",
                bround(sum("todayDeaths") / sum("deaths") * 100, 2) as "Death Percent Change",
                sum("recovered") as "Total Recoveries",
                sum("todayRecovered") as "Today's Recoveries",
                bround(sum("todayRecovered") / sum("recovered") * 100, 2) as "Recoveries Percent Change")

            //Creates final South America DF
            val southAmerica = southAmericaTemp.select(
                lit("South America").as("Region"),
                sum("cases") as "Total Cases",
                sum("todayCases") as "Today's Cases",
                bround(sum("todayCases") / sum("cases") * 100, 2) as "Cases Percent Change",
                sum("deaths") as "Total Deaths",
                sum("todayDeaths") as "Today's Deaths",
                bround(sum("todayDeaths") / sum("deaths") * 100, 2) as "Death Percent Change",
                sum("recovered") as "Total Recoveries",
                sum("todayRecovered") as "Today's Recoveries",
                bround(sum("todayRecovered") / sum("recovered") * 100, 2) as "Recoveries Percent Change")

            //Union to combine all regional DataFrames into global DataFrame
            val regionsTemp = africa.union(asia)
                .union(caribbean)
                .union(centralAmerica)
                .union(europe)
                .union(northAmerica)
                .union(oceania)
                .union(southAmerica)
                .sort(desc("Cases Percent Change"))

            val totals = regionsTemp.select(
                lit("Total").as("Region"),
                sum("Total Cases"),
                sum("Today's Cases"),
                bround(avg("Cases Percent Change"), 2),
                sum("Total Deaths"),
                sum("Today's Deaths"),
                bround(avg("Death Percent Change"), 2),
                sum("Total Recoveries"),
                sum("Today's Recoveries"),
                bround(avg("Recoveries Percent Change"), 2))

            val regions = regionsTemp.union(totals)

            //Displays table of regions global DataFrame in order of largest to smallest percent cases change
            regions
                .show()

        }

        //Set Variable Timestamp for Batch Number
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        var x = LocalDateTime.now().format(formatter)
        //Executes Program on a loop once every 10 minutes to update table w/new data
        while (true) {
            val milliseconds = System.currentTimeMillis();
            x = LocalDateTime.now().format(formatter)
            println(s"Last Updated: ${x}")
            println("==================================")
            dataProcessing
            Thread.sleep(600000 - milliseconds % 1000)

        }
    }

}

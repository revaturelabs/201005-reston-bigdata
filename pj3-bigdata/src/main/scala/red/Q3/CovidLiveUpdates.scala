package RedTeam.Q3

import java.io.{File, PrintWriter}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.jsoup.Jsoup

object CovidLiveUpdates {
  def main(args: Array[String]): Unit = {

    //Spark Session
    val spark = SparkSession.builder()
      .appName("Red Team - Q3 Live Stream")
      .master("local[4]")
      .getOrCreate()

    //Set log level for sbt shell
    spark.sparkContext.setLogLevel("ERROR")

    //Runs API calls to update all regional data
    def dataUpdate: Unit = {

      //Updates Africa regional data
      val json1 = Jsoup.connect("https://disease.sh/v3/covid-19/countries/Algeria%2CAngola%2CBenin%2CBotswana%2" +
        "CBurkinaFaso%2CBurundi%2CCameroon%2CCapeVerde%2CCentral%20African%20Republic%2CChad%2CComoros%2CCote%20d'Ivoire%2C" +
        "Democratic%20Republic%20of%20the%20Congo%2CDjibouti%2CEgypt%2CEquatorial%20Guinea%2CEritrea%2CEthiopia%2CGabon%2CGambia%2C" +
        "Ghana%2CGuinea%2CGuinea-Bissau%2CKenya%2CLesotho%2CLiberia%2CLibya%2CMadagascar%2CMalawi%2CMali%2C" +
        "Mauritania%2CMauritius%2CMorocco%2CMozambique%2CNamibia%2CNiger%2CNigeria%2CRepublic%20of%20the%20Congo%2CReunion%2C" +
        "Rwanda%2CSaint%20Helena%2CSao%20Tome%20and%20Principe%2CSenegal%2CSeychelles%2CSierra%20Leone%2CSomalia%2CSouth%20Africa%2C" +
        "South%20Sudan%2CSudan%2CSwaziland%2CTanzania%2CTogo%2CTunisia%2CUganda%2CWestern%20Sahara%2CZambia%2CZimbabwe"
      ).ignoreContentType(true).execute.body
      val pw1 = new PrintWriter(new File("data/africa.json"))
      pw1.write(json1)
      pw1.close()

      //Updates Asia regional data
      val json2 = Jsoup.connect("https://disease.sh/v3/covid-19/countries/Afghanistan%2CArmenia%2C" +
        "Azerbaijan%2CBahrain%2CBangladesh%2CBhutan%2CBrunei%2CBurma%2CCambodia%2CChina%2CCyprus%2CEast%20Timor%2C" +
        "Georgia%2CHong%20Kong%2CIndia%2CIndonesia%2CIran%2CIraq%2CIsrael%2CJapan%2CJordan%2CKazakhstan%2CKuwait%2C" +
        "Kyrgyzstan%2CLaos%2CLebanon%2CMacau%2CMalaysia%2CMaldives%2CMongolia%2CNepal%2CNorth%20Korea%2COman%2C" +
        "Pakistan%2CPhilippines%2CQatar%2CSaudi%20Arabia%2CSingapore%2CSouth%20Korea%2CSri%20Lanka%2CSyria%2CTaiwan%2C" +
        "Tajikistan%2CThailand%2CTurkey%2CTurkmenistan%2CUnited%20Arab%20Emirates%2CUzbekistan%2CVietnam%2CYemen"
      ).ignoreContentType(true).execute.body
      val pw2 = new PrintWriter(new File("data/asia.json"))
      pw2.write(json2)
      pw2.close()

      //Updates Carribean regional data
      val json3 = Jsoup.connect("https://disease.sh/v3/covid-19/countries/Anguilla%2CAntigua%20and%20Barbuda%2C" +
        "Aruba%2CThe%20Bahamas%2CBarbados%2CBermuda%2CBritish%20Virgin%20Islands%2CCayman%20Islands%2CCuba%2CDominica%2C" +
        "Dominican%20Republic%2CGrenada%2CGuadeloupe%2CHaiti%2CJamaica%2CMartinique%2CMontserrat%2C" +
        "Netherlands%20Antilles%2CPuerto%20Rico%2CSaint%20Kitts%20and%20Nevis%2CSaint%20Lucia%2CSaint%20Vincent%20and%20the%20Grenadines%2C" +
        "Trinidad%20and%20Tobago%2CTurks%20and%20Caicos%20Islands%2CU.S.%20Virgin%20Islands"
      ).ignoreContentType(true).execute.body
      val pw3 = new PrintWriter(new File("data/caribbean.json"))
      pw3.write(json3)
      pw3.close()

      //Updates Central America regional data
      val json4 = Jsoup.connect("https://disease.sh/v3/covid-19/countries/Belize%2CCosta%20Rica%2CEl%20Salvador%2C" +
        "Guatemala%2CHonduras%2CNicaragua%2CPanama"
      ).ignoreContentType(true).execute.body
      val pw4 = new PrintWriter(new File("data/central_america.json"))
      pw4.write(json4)
      pw4.close()

      //Updates Europe regional data
      val json5 = Jsoup.connect("https://disease.sh/v3/covid-19/countries/Albania%2CAndorra%2CAustria%2C" +
        "Belarus%2CBelgium%2CBosnia%20and%20Herzegovina%2CBulgaria%2CCroatia%2CCzech%20Republic%2CDenmark%2CEstonia%2C" +
        "Finland%2CFrance%2CGermany%2CGibraltar%2CGreece%2CHoly%20See%2CHungary%2CIceland%2CIreland%2CItaly%2C" +
        "Kosovo%2CLatvia%2CLiechtenstein%2CLithuania%2CLuxembourg%2CMacedonia%2CMalta%2CMoldova%2CMonaco%2C" +
        "Montenegro%2CNetherlands%2CNorway%2CPoland%2CPortugal%2CRomania%2CRussia%2CSan%20Marino%2CSlovak%20Republic%2C" +
        "Slovenia%2CSpain%2CSerbia%2CSerbia%20and%20Montenegro%2CSweden%2CSwitzerland%2CUkraine%2CUnited%20Kingdom"
      ).ignoreContentType(true).execute.body
      val pw5 = new PrintWriter(new File("data/europe.json"))
      pw5.write(json5)
      pw5.close()

      //Updates North America regional data
      val json6 = Jsoup.connect("https://disease.sh/v3/covid-19/countries/Canada%2CGreenland%2CMexico%2C" +
        "Saint%20Pierre%20and%20Miquelon%2CUnited%20States"
      ).ignoreContentType(true).execute.body
      val pw6 = new PrintWriter(new File("data/north_america.json"))
      pw6.write(json6)
      pw6.close()

      //Updates Oceania regional data
      val json7 = Jsoup.connect("https://disease.sh/v3/covid-19/countries/AmericanSamoa%2CAustralia%2C" +
        "Christmas%20Island%2CCocos%20(Keeling)%20Islands%2CCook%20Islands%2CFederated%20States%20of%20Micronesia%2CFiji%2C" +
        "French%20Polynesia%2CGuam%2CKiribati%2CMarshall%20Islands%2CNauru%2CNew%20Caledonia%2CNew%20Zealand%2CNiue%2C" +
        "Northern%20Mariana%20Islands%2CPalau%2CPapua%20New%20Guinea%2CPitcairn%20Islands%2CSamoa%2CSolomon%20Islands%2CTokelau%2C" +
        "Tonga%2CTuvalu%2CVanuatu%2CWallis%20and%20Futuna"
      ).ignoreContentType(true).execute.body
      val pw7 = new PrintWriter(new File("data/oceania.json"))
      pw7.write(json7)
      pw7.close()

      //Updates South America regional data
      val json8 = Jsoup.connect("https://disease.sh/v3/covid-19/countries/Argentina%2CBolivia%2CBrazil%2C" +
        "Chile%2CColombia%2CEcuador%2CFalkland%20Islands%2CFrench%20Guiana%2CGuyana%2CParaguay%2CPeru%2CSuriname%2C" +
        "Uruguay%2CVenezuela"
      ).ignoreContentType(true).execute.body
      val pw8 = new PrintWriter(new File("data/south_america.json"))
      pw8.write(json8)
      pw8.close()


    }

    //Processes data and performs union to create single regions DataFrame
    def dataProcessing = {
      //Calls DataUpdate to provide up to date stats for all regions
      dataUpdate

      //Creates a DataFrame from JSON for each region
      val africaTemp = spark.read.json("data/africa.json")
      val asiaTemp = spark.read.json("data/asia.json")
      val caribbeanTemp = spark.read.json("data/caribbean.json")
      val centralAmericaTemp = spark.read.json("data/central_america.json")
      val europeTemp = spark.read.json("data/europe.json")
      val northAmericaTemp = spark.read.json("data/north_america.json")
      val oceaniaTemp = spark.read.json("data/oceania.json")
      val southAmericaTemp = spark.read.json("data/south_america.json")

      //Spark SQL to select proper columns from each DF and save as new DF

      //Creates final Africa DF
      val africa = africaTemp.select(from_unixtime(first(col("updated")) / 1000, "MM-dd-yyyy") as "Date",
        lit("Africa").as("Region"),
        sum("cases") as "Total Cases",
        sum("todayCases") as "Today Cases",
        bround(sum("todayCases") / sum("cases") * 100, 2) as "Cases Percent Change",
        sum("deaths") as "Total Deaths",
        sum("todayDeaths") as "Current Day Deaths",
        bround(sum("todayDeaths") / sum("deaths") * 100, 2) as "Death Percent Change",
        sum("recovered") as "Total Recoveries",
        sum("todayRecovered") as "Current Day Recoveries",
        bround(sum("todayRecovered") / sum("recovered") * 100, 2) as "Recoveries Percent Change")

      //Creates final Asia DF
      val asia = asiaTemp.select(from_unixtime(first(col("updated")) / 1000, "MM-dd-yyyy") as "Date",
        lit("Asia").as("Region"),
        sum("cases") as "Total Cases",
        sum("todayCases") as "Today Cases",
        bround(sum("todayCases") / sum("cases") * 100, 2) as "Cases Percent Change",
        sum("deaths") as "Total Deaths",
        sum("todayDeaths") as "Current Day Deaths",
        bround(sum("todayDeaths") / sum("deaths") * 100, 2) as "Death Percent Change",
        sum("recovered") as "Total Recoveries",
        sum("todayRecovered") as "Current Day Recoveries",
        bround(sum("todayRecovered") / sum("recovered") * 100, 2) as "Recoveries Percent Change")

      //Creates final Carribean DF
      val caribbean = caribbeanTemp.select(from_unixtime(first(col("updated")) / 1000, "MM-dd-yyyy") as "Date",
        lit("Caribbean").as("Region"),
        sum("cases") as "Total Cases",
        sum("todayCases") as "Today Cases",
        bround(sum("todayCases") / sum("cases") * 100, 2) as "Cases Percent Change",
        sum("deaths") as "Total Deaths",
        sum("todayDeaths") as "Current Day Deaths",
        bround(sum("todayDeaths") / sum("deaths") * 100, 2) as "Death Percent Change",
        sum("recovered") as "Total Recoveries",
        sum("todayRecovered") as "Current Day Recoveries",
        bround(sum("todayRecovered") / sum("recovered") * 100, 2) as "Recoveries Percent Change")

      //Creates final Central America DF
      val centralAmerica = centralAmericaTemp.select(from_unixtime(first(col("updated")) / 1000, "MM-dd-yyyy") as "Date",
        lit("Central America").as("Region"),
        sum("cases") as "Total Cases",
        sum("todayCases") as "Today Cases",
        bround(sum("todayCases") / sum("cases") * 100, 2) as "Cases Percent Change",
        sum("deaths") as "Total Deaths",
        sum("todayDeaths") as "Current Day Deaths",
        bround(sum("todayDeaths") / sum("deaths") * 100, 2) as "Death Percent Change",
        sum("recovered") as "Total Recoveries",
        sum("todayRecovered") as "Current Day Recoveries",
        bround(sum("todayRecovered") / sum("recovered") * 100, 2) as "Recoveries Percent Change")

      //Creates final Europe DF
      val europe = europeTemp.select(from_unixtime(first(col("updated")) / 1000, "MM-dd-yyyy") as "Date",
        lit("Europe").as("Region"),
        sum("cases") as "Total Cases",
        sum("todayCases") as "Today Cases",
        bround(sum("todayCases") / sum("cases") * 100, 2) as "Cases Percent Change",
        sum("deaths") as "Total Deaths",
        sum("todayDeaths") as "Current Day Deaths",
        bround(sum("todayDeaths") / sum("deaths") * 100, 2) as "Death Percent Change",
        sum("recovered") as "Total Recoveries",
        sum("todayRecovered") as "Current Day Recoveries",
        bround(sum("todayRecovered") / sum("recovered") * 100, 2) as "Recoveries Percent Change")

      //Creates final North America DF
      val northAmerica = northAmericaTemp.select(from_unixtime(first(col("updated")) / 1000, "MM-dd-yyyy") as "Date",
        lit("North America").as("Region"),
        sum("cases") as "Total Cases",
        sum("todayCases") as "Today Cases",
        bround(sum("todayCases") / sum("cases") * 100, 2) as "Cases Percent Change",
        sum("deaths") as "Total Deaths",
        sum("todayDeaths") as "Current Day Deaths",
        bround(sum("todayDeaths") / sum("deaths") * 100, 2) as "Death Percent Change",
        sum("recovered") as "Total Recoveries",
        sum("todayRecovered") as "Current Day Recoveries",
        bround(sum("todayRecovered") / sum("recovered") * 100, 2) as "Recoveries Percent Change")

      //Creates final Oceania DF
      val oceania = oceaniaTemp.select(from_unixtime(first(col("updated")) / 1000, "MM-dd-yyyy") as "Date",
        lit("Oceania").as("Region"),
        sum("cases") as "Total Cases",
        sum("todayCases") as "Today Cases",
        bround(sum("todayCases") / sum("cases") * 100, 2) as "Cases Percent Change",
        sum("deaths") as "Total Deaths",
        sum("todayDeaths") as "Current Day Deaths",
        bround(sum("todayDeaths") / sum("deaths") * 100, 2) as "Death Percent Change",
        sum("recovered") as "Total Recoveries",
        sum("todayRecovered") as "Current Day Recoveries",
        bround(sum("todayRecovered") / sum("recovered") * 100, 2) as "Recoveries Percent Change")

      //Creates final South America DF
      val southAmerica = southAmericaTemp.select(from_unixtime(first(col("updated")) / 1000, "MM-dd-yyyy") as "Date",
        lit("South America").as("Region"),
        sum("cases") as "Total Cases",
        sum("todayCases") as "Today Cases",
        bround(sum("todayCases") / sum("cases") * 100, 2) as "Cases Percent Change",
        sum("deaths") as "Total Deaths",
        sum("todayDeaths") as "Current Day Deaths",
        bround(sum("todayDeaths") / sum("deaths") * 100, 2) as "Death Percent Change",
        sum("recovered") as "Total Recoveries",
        sum("todayRecovered") as "Current Day Recoveries",
        bround(sum("todayRecovered") / sum("recovered") * 100, 2) as "Recoveries Percent Change")

      //Union to combine all regional DataFrames into global DataFrame
      val regions = africa.union(asia)
        .union(caribbean)
        .union(centralAmerica)
        .union(europe)
        .union(northAmerica)
        .union(oceania)
        .union(southAmerica)

      //Displays table of regions global DataFrame in order of largest to smallest percent cases change
      regions
        .sort(desc("Cases Percent Change"))
        .show()

    }

    //Set Variable Timestamp for Batch Number
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    var x = LocalDateTime.now().format(formatter)
    //Executes Program on a loop once every 10 minutes to update table w/new data
    while (true) {
      val milliseconds = System.currentTimeMillis();
      x = LocalDateTime.now().format(formatter)
      println(s"Batch: ${x}")
      dataProcessing
      Thread.sleep(600000 - milliseconds % 1000)

    }
  }
}

package orange

import org.apache.spark
import org.apache.spark.SparkConf
import org.apache.spark.sql.functions.{asc, desc, round}
import org.apache.spark.sql.{DataFrame, SparkSession, functions}
object orangeRunner {
  def main(args: Array[String]): Unit={
    val appName = "Orange"
    val spark = SparkSession.builder()
      .appName(appName)
      .master("local[4]")
      .getOrCreate()
    import spark.implicits._
    spark.sparkContext.setLogLevel("WARN")

    //import the covid data from testData file as well as the border data from the provided border data
    val country_stats = spark.read.option("multiline", "true").option("header", "true").option("sep", "\t").csv("countries_general_stats.tsv")
    val country_data = spark.read.option("multiline", "true").option("header", "true").option("sep", "\t").csv("daily_stats.tsv")

    val country_pop = country_stats.select($"COUNTRY", $"POPULATION".cast("Int"))

    val country_cases = country_data
      .filter($"TOTAL_CASES" =!= "NULL")
      .withColumn("Cases", $"TOTAL_CASES".cast("Int"))
      .select($"COUNTRY", $"Cases".as("Total Cases"))
      .groupBy($"COUNTRY")
      .agg(functions.max($"Total Cases").as("TOTAL CASES"))
      .sort(functions.asc("COUNTRY"))


    val borders = joinCodesAndBorders(spark)
//    borders.show(100)

    //create "infection_rate" from covid data directory with daily covid data
    val infection_rate = country_cases
      .join(country_pop, country_cases("COUNTRY") === country_pop("COUNTRY"))
      .select(country_cases("COUNTRY"), $"TOTAL CASES", $"POPULATION",
        ($"TOTAL CASES" * 100 / $"POPULATION").as("infection_rate_per_capita"))
      .sort(functions.desc("infection_rate_per_capita"))
      .withColumn("infection_rate_per_capita",'infection_rate_per_capita.cast("Decimal(5,3)"))


    /*
  bcountries have land borders
     */
    val bcountries = borders.join(infection_rate, borders("country_name") === infection_rate("COUNTRY"), "right")
      .select(infection_rate("COUNTRY").as("country_name"),  borders("border_country"), borders("border_country_code"),
        infection_rate("infection_rate_per_capita").as("country_infection_rate"))
//        bcountries.show(1000)
    //first.printSchema()


    /*
    the result of joining our border countries with our infection data. This gives us information about the home country, its infection rate,
    bordering countries and their infection rate, and a delta value, which is the difference between these 2 rates.
     */
    val res1 = bcountries.join(infection_rate, bcountries("border_country") === infection_rate("COUNTRY"), "inner")
      .select(bcountries("country_name"), bcountries("border_country"), bcountries("border_country_code"),
        bcountries("country_infection_rate"),
        infection_rate("infection_rate_per_capita").as("country_border_infection_rate_per_capita"),
        //round(bcountries("country_infection_rate") - infection_rate("infection_rate_per_capita"), 2).as("delta"))
        (bcountries("country_infection_rate") - infection_rate("infection_rate_per_capita")).as("delta"))
        //res.show(10)


    /*
    Part 2 of question is asking about land and water locked countries, use dataframes defined below to create dataframes with land/water locked countries
    and combine them
     */
    val landLocked = createLandLocked(spark)
    //    landLocked.show(10)

    val waterLocked = createWaterLocked(bcountries, spark)
//        waterLocked.show(10)

    //combine landLocked dataframe with infection_rate to make a dataframe with the infection rate of land locked countries
    val landLockedInfRate = landLocked.join(infection_rate, landLocked("country_name") === infection_rate("COUNTRY"), "inner")
      .select(landLocked("country_name"), landLocked("country_code"),
        infection_rate("infection_rate_per_capita").as("infection_rate_per_capita(%)"))

    //combine waterLocked dataframe with infection_rate to make a dataframe with the infection rate of water locked countries
    val waterLockedInfRate = waterLocked.join(infection_rate, waterLocked("country_name") === infection_rate("COUNTRY"), "inner")
      .select(waterLocked("country_name"),
        infection_rate("infection_rate_per_capita").as("infection_rate_per_capita(%)"))
       //waterLockedInfRate.show(10)



    //RESULT QUERIES

    /*
    query to give result to part 1. by using delta > 0, we can ensure no duplicates of countries in opposite directions, such as
    having both Israel-Lebanon and Lebanon-Israel.
     */
    println("Largest Difference(Delta) in Bordering Countries")
    res1.select("*")
      .where($"delta" > 0)
      .orderBy(desc("delta")) //using desc here allows us to get the largest differences at the top, and smaller differences at the end
      .show(10)

    /* Queries to give us the answer to the second part of our question. Using the Dataframes for land and water locked countries, we can do simple
    queries to give the required answers
     */
    //       landLockedInfRate.select("*")
    //       .orderBy(asc("infection_rate_per_capita(%)"))
    //         .show(10)

    println("Highest Infection Rate in Land Locked Countries\n")
    landLockedInfRate.select("*")
      .orderBy(desc("infection_rate_per_capita(%)"))
      .show(10)

    println("Highest Infection Rate in Water Locked Countries\n")
    waterLockedInfRate.select("*")
      .orderBy(desc("infection_rate_per_capita(%)"))
      .show(10)

    // second.printSchema()

    //second.show(50)

  }

  //  //TODO: adapt to accept the landlocked_countries_list(only need val landlocked since we don't really care about doubly land locked, and they're already included in the landlocked list
  //creates a dataframe of LandLocked countries from given the given landlocked.csv
  def createLandLocked(spark:SparkSession): DataFrame ={
    val landLocked =spark.read.options(Map("inferSchema"->"true","delimiter"->",","header"->"true")).csv("landlocked.csv")
    landLocked
  }

  //uses the dataframe of our border countries and looks for there to be a NULL for border country. This means there is no land border, meaning the country is waterlocked
  def createWaterLocked(infectionFrame: DataFrame , spark:SparkSession): DataFrame ={
    import spark.implicits._
    val waterLocked = infectionFrame.filter($"border_country".isNull)
      .select($"country_name")
    waterLocked
  }

  /**
   * Takes the border dictionary and the country code dictionary from the dictionaries class and creates
   *  dataframes using both of them.  Those dataframes are then joined to create a dataframe with the
   *  following format:
   *  country_name, country_code, border_country, and country_border_code
   *
   *
   * @param spark is the spark session that is used in the method
   * @return is the final dataframe that is the combination of the two dictionaries.
   */
  def joinCodesAndBorders(spark: SparkSession) = {

    import spark.implicits._

    spark.sparkContext.setLogLevel("WARN")

    //creates a dataframe from the border dictionary in the dictionaries class

    val borders_dictionary = dictionaries.getBorders()

    val borders = borders_dictionary.toSeq.toDF("country", "borders")
      .select($"country", functions.explode($"borders").as("border"))

    //creates a dataframe from the country code dictionary in the dictionaries class
    val country_codes_a2 = dictionaries.getCountryCodes()

    val codes = country_codes_a2.toSeq.toDF("code", "country")

    //borders.show()
    //codes.show()

    //joins the border dictionary with the country code dictionary on the name of the key in the borders dictionary
    // to create a dataframe that contains the following format:
    //  country_name, country_code, border_country

    val first = borders.join(codes, borders("country") === codes("country"), "inner")
      .select(borders("country").as("country_name"), codes("code").as("country_code"),
        borders("border").as("border_country"))


    //joins the dataframe that was just created to the country code dictionary on the value in the country code to
    //  create a dataframe that contains the following format:
    //  country_name, country_code, border_country, border_country_code
    val second = first.join(codes, first("border_country") === codes("country"), "inner")
      .select(first("country_name"), first("country_code"), first("border_country"),
        codes("code").as("border_country_code"))

    //returns the final dataframe so it can be used in other queries in the application
    second
  }

}

//todo:make these separate, we dont want a 1500 line main file -_-
object dictionaries {

  def getCountryCodes(): Map[String, String] = {
    val country_codes_a2 = Map[String, String](
      "AF" -> 	"Afghanistan",
      "AL" -> 	"Albania",
      "DZ" -> 	"Algeria",
      "AS" -> 	"American Samoa",
      "AD" -> 	"Andorra",
      "AO" -> 	"Angola",
      "AI" -> 	"Anguilla",
      "AQ" -> 	"Antarctica",
      "AG" -> 	"Antigua and Barbuda",
      "AR" -> 	"Argentina",
      "AM" -> 	"Armenia",
      "AW" -> 	"Aruba",
      "AU" -> 	"Australia",
      "AT" -> 	"Austria",
      "AZ" -> 	"Azerbaijan",
      "BS" -> 	"Bahamas",
      "BH" -> 	"Bahrain",
      "BD" -> 	"Bangladesh",
      "BB" -> 	"Barbados",
      "BY" -> 	"Belarus",
      "BE" -> 	"Belgium",
      "BZ" -> 	"Belize",
      "BJ" -> 	"Benin",
      "BM" -> 	"Bermuda",
      "BT" -> 	"Bhutan",
      "BO" -> 	"Bolivia",
      "BQ" -> 	"Bonaire Sint Eustatius and Saba", //correct spelling
      "BA" -> 	"Bosnia and Herzegovina",
      "BW" -> 	"Botswana",
      "BV" -> 	"Bouvet Island", //no data
      "BR" -> 	"Brazil",
      "IO" -> 	"British Indian Ocean Territory",
      "BN" -> 	"Brunei",
      "BG" -> 	"Bulgaria",
      "BF" -> 	"Burkina Faso",
      "BI" -> 	"Burundi",
      "CV" -> 	"Cabo Verde", //no data
      "KH" -> 	"Cambodia",
      "CM" -> 	"Cameroon",
      "CA" -> 	"Canada",
      "KY" -> 	"Cayman Islands",
      "CF" -> 	"Central African Republic",
      "TD" -> 	"Chad",
      "CL" -> 	"Chile",
      "CN" -> 	"China",
      "CX" -> 	"Christmas Island",
      "CC" -> 	"Cocos (Keeling) Islands",
      "CO" -> 	"Colombia",
      "KM" -> 	"Comoros",
      "CD" -> 	"Democratic Republic of the Congo",
      "CG" -> 	"Congo",
      "CK" -> 	"Cook Islands",
      "CR" -> 	"Costa Rica",
      "HR" -> 	"Croatia",
      "CU" -> 	"Cuba",
      "CW" -> 	"Curacao",
      "CY" -> 	"Cyprus",
      "CZ" -> 	"Czechia",
      "CI" -> 	"Cote d'Ivoire", //correct spelling
      "DK" -> 	"Denmark",
      "DJ" -> 	"Djibouti",
      "DM" -> 	"Dominica",
      "DO" -> 	"Dominican Republic",
      "EC" -> 	"Ecuador",
      "EG" -> 	"Egypt",
      "SV" -> 	"El Salvador",
      "GQ" -> 	"Equatorial Guinea",
      "ER" -> 	"Eritrea",
      "EE" -> 	"Estonia",
      "SZ" -> 	"Eswatini",
      "ET" -> 	"Ethiopia",
      "FK" -> 	"Falkland Islands",
      "FO" -> 	"Faeroe Islands",
      "FJ" -> 	"Fiji",
      "FI" -> 	"Finland",
      "FR" -> 	"France",
      "GF" -> 	"French Guiana",
      "PF" -> 	"French Polynesia",
      "TF" -> 	"French Southern Territories",
      "GA" -> 	"Gabon",
      "GM" -> 	"Gambia",
      "GE" -> 	"Georgia",
      "DE" -> 	"Germany",
      "GH" -> 	"Ghana",
      "GI" -> 	"Gibraltar",
      "GR" -> 	"Greece",
      "GL" -> 	"Greenland",
      "GD" -> 	"Grenada",
      "GP" -> 	"Guadeloupe",
      "GU" -> 	"Guam",
      "GT" -> 	"Guatemala",
      "GG" -> 	"Guernsey",
      "GN" -> 	"Guinea",
      "GW" -> 	"Guinea-Bissau",
      "GY" -> 	"Guyana",
      "HT" -> 	"Haiti",
      "HM" -> 	"Heard Island and McDonald Islands",
      "VA" -> 	"Holy See",
      "HN" -> 	"Honduras",
      "HK" -> 	"Hong Kong",
      "HU" -> 	"Hungary",
      "IS" -> 	"Iceland",
      "IN" -> 	"India",
      "ID" -> 	"Indonesia",
      "IR" -> 	"Iran",
      "IQ" -> 	"Iraq",
      "IE" -> 	"Ireland",
      "IM" -> 	"Isle of Man",
      "IL" -> 	"Israel",
      "IT" -> 	"Italy",
      "JM" -> 	"Jamaica",
      "JP" -> 	"Japan",
      "JE" -> 	"Jersey",
      "JO" -> 	"Jordan",
      "KZ" -> 	"Kazakhstan",
      "KE" -> 	"Kenya",
      "KI" -> 	"Kiribati",
      "KP" -> 	"North Korea",
      "KR" -> 	"South Korea",
      "KW" -> 	"Kuwait",
      "KG" -> 	"Kyrgyzstan",
      "LA" -> 	"Laos",
      "LV" -> 	"Latvia",
      "LB" -> 	"Lebanon",
      "LS" -> 	"Lesotho",
      "LR" -> 	"Liberia",
      "LY" -> 	"Libya",
      "LI" -> 	"Liechtenstein",
      "LT" -> 	"Lithuania",
      "LU" -> 	"Luxembourg",
      "MO" -> 	"Macao",
      "MG" -> 	"Madagascar",
      "MW" -> 	"Malawi",
      "MY" -> 	"Malaysia",
      "MV" -> 	"Maldives",
      "ML" -> 	"Mali",
      "MT" -> 	"Malta",
      "MH" -> 	"Marshall Islands",
      "MQ" -> 	"Martinique",
      "MR" -> 	"Mauritania",
      "MU" -> 	"Mauritius",
      "YT" -> 	"Mayotte", //no data
      "MX" -> 	"Mexico",
      "FM" -> 	"Micronesia",
      "MD" -> 	"Moldova",
      "MC" -> 	"Monaco",
      "MN" -> 	"Mongolia",
      "ME" -> 	"Montenegro",
      "MS" -> 	"Montserrat",
      "MA" -> 	"Morocco",
      "MZ" -> 	"Mozambique",
      "MM" -> 	"Myanmar",
      "NA" -> 	"Namibia",
      "NR" -> 	"Nauru",
      "NP" -> 	"Nepal",
      "NL" -> 	"Netherlands",
      "NC" -> 	"New Caledonia",
      "NZ" -> 	"New Zealand",
      "NI" -> 	"Nicaragua",
      "NE" -> 	"Niger", //correct spelling
      "NG" -> 	"Nigeria",
      "NU" -> 	"Niue", //no data
      "NF" -> 	"Norfolk Island",
      "MP" -> 	"Northern Mariana Islands",
      "NO" -> 	"Norway",
      "OM" -> 	"Oman",
      "PK" -> 	"Pakistan",
      "PW" -> 	"Palau", //no data
      "PS" -> 	"Palestine",
      "PA" -> 	"Panama",
      "PG" -> 	"Papua New Guinea",
      "PY" -> 	"Paraguay",
      "PE" -> 	"Peru",
      "PH" -> 	"Philippines",
      "PN" -> 	"Pitcairn",
      "PL" -> 	"Poland",
      "PT" -> 	"Portugal",
      "PR" -> 	"Puerto Rico",
      "QA" -> 	"Qatar",
      "MK" -> 	"Macedonia",
      "RO" -> 	"Romania",
      "RU" -> 	"Russia",
      "RW" -> 	"Rwanda",
      "RE" -> 	"Reunion",
      "BL" -> 	"Saint Barthelemy",
      "SH" -> 	"Saint Helena",
      "KN" -> 	"Saint Kitts and Nevis",
      "LC" -> 	"Saint Lucia",
      "MF" -> 	"Saint Martin (French part)",
      "PM" -> 	"Saint Pierre and Miquelon",
      "VC" -> 	"Saint Vincent and the Grenadines",
      "WS" -> 	"Samoa",
      "SM" -> 	"San Marino",
      "ST" -> 	"Sao Tome and Principe",
      "SA" -> 	"Saudi Arabia",
      "SN" -> 	"Senegal",
      "RS" -> 	"Serbia",
      "SC" -> 	"Seychelles",
      "SL" -> 	"Sierra Leone",
      "SG" -> 	"Singapore",
      "SX" -> 	"Sint Maarten (Dutch part)", //correct spelling
      "SK" -> 	"Slovakia",
      "SI" -> 	"Slovenia",
      "SB" -> 	"Solomon Islands",
      "SO" -> 	"Somalia",
      "ZA" -> 	"South Africa",
      "GS" -> 	"South Georgia and the South Sandwich Islands",
      "SS" -> 	"South Sudan",
      "ES" -> 	"Spain",
      "LK" -> 	"Sri Lanka",
      "SD" -> 	"Sudan",
      "SR" -> 	"Suriname",
      "SJ" -> 	"Svalbard and Jan Mayen", //no data
      "SE" -> 	"Sweden",
      "CH" -> 	"Switzerland",
      "SY" -> 	"Syria",
      "TW" -> 	"Taiwan",
      "TJ" -> 	"Tajikistan",
      "TZ" -> 	"Tanzania",
      "TH" -> 	"Thailand",
      "TL" -> 	"Timor-Leste",
      "TG" -> 	"Togo",
      "TK" -> 	"Tokelau", //no data
      "TO" -> 	"Tonga",
      "TT" -> 	"Trinidad and Tobago",
      "TN" -> 	"Tunisia",
      "TR" -> 	"Turkey",
      "TM" -> 	"Turkmenistan",
      "TC" -> 	"Turks and Caicos Islands", //correct spelling
      "TV" -> 	"Tuvalu",
      "UG" -> 	"Uganda",
      "UA" -> 	"Ukraine",
      "AE" -> 	"United Arab Emirates",
      "GB" -> 	"United Kingdom",
      "UM" -> 	"United States Minor Outlying Islands (the)",
      "US" -> 	"United States",
      "UY" -> 	"Uruguay",
      "UZ" -> 	"Uzbekistan",
      "VU" -> 	"Vanuatu",
      "VE" -> 	"Venezuela",
      "VN" -> 	"Vietnam",
      "VG" -> 	"Virgin Islands (British)",
      "VI" -> 	"Virgin Islands",
      "WF" -> 	"Wallis and Futuna", //correct spelling
      "EH" -> 	"Western Sahara",
      "YE" -> 	"Yemen",
      "ZM" -> 	"Zambia",
      "ZW" -> 	"Zimbabwe",
      "AX" -> 	"Aland Islands"
    )
    country_codes_a2
  }

  //Returns the dictionary containing a key-value pair every pair of countries that border each other
  //  with the key being a certain country and the value being a list of every country that borders said
  //  country

  def getBorders(): Map[String, List[String]] = {
    val borders_dictionary = Map[String, List[String]](
      "Andorra" -> List(
        "France",
        "Spain"
      ),
      "United Arab Emirates" -> List(
        "Oman",
        "Saudi Arabia"
      ),
      "Afghanistan" -> List(   //Afgahnastan
        "China",
        "Iran",
        "Pakistan",
        "Tajikistan",       //tajikastan
        "Turkmenistan",
        "Uzbekistan"
      ),
      "Antigua and Barbuda" -> List(),
      "Anguilla" -> List(),
      "Albania" -> List(
        "Greece",
        "Montenegro",
        "Macedonia",
        "Serbia"
      ),
      "Armenia" -> List(
        "Azerbaijan",
        "Georgia",    //gerogia
        "Iran",
        "Turkey"
      ),
      "Angola" -> List(
        "Congo",
        "Democratic Republic of Congo",   //repbulic
        "Namibia", //nambia
        "Zambia"
      ),
      "Antarctica" -> List(),
      "Argentina" -> List(
        "Bolivia",
        "Brazil",
        "Chile",
        "Paraguay",
        "Uruguay"
      ),
      "American Samoa" -> List(),
      "Austria" -> List(
        "Czechia",
        "Germany",
        "Hungary",
        "Italy",
        "Liechtenstein",  //Lietchenstien
        "Slovakia",
        "Slovenia",
        "Switzerland"
      ),
      "Australia" -> List(),
      "Aruba" -> List(),
      "Aland Islands" -> List(),
      "Azerbaijan" -> List(
        "Armenia",
        "Georgia",
        "Iran",
        "Russia",
        "Turkey"
      ),
      "Bosnia and Herzegovina" -> List(
        "Croatia",
        "Montenegro",
        "Serbia"
      ),
      "Barbados" -> List(),
      "Bangladesh" -> List(
        "India",
        "Myanmar"
      ),
      "Belgium" -> List(
        "France",
        "Germany",
        "Luxembourg",
        "Netherlands"
      ),
      "Burkina Faso" -> List(
        "Benin",
        "Cote d'Ivoire", //correct spelling
        "Ghana",
        "Mali",
        "Niger",  //correct spelling
        "Togo"
      ),
      "Bulgaria" -> List(
        "Greece",
        "Macedonia",
        "Romania",
        "Serbia",
        "Turkey"
      ),
      "Bahrain" -> List(),
      "Burundi" -> List(
        "Congo",
        "Rwanda",
        "Tanzania"
      ),
      "Benin" -> List(
        "Burkina Faso",
        "Niger",
        "Nigeria",
        "Togo"
      ),
      "Saint Barthelemy" -> List(), //Saint Bathelemy
      "Bermuda" -> List(),
      "Brunei" -> List(
        "Malaysia"
      ),
      "Bolivia" -> List(
        "Argentina",
        "Brazil",
        "Chile",
        "Paraguay",
        "Peru"
      ),
      "Bonaire Sint Eustatius and Saba" -> List(), //correct spelling
      "Brazil" -> List(
        "Argentina",
        "Bolivia",
        "Colombia",
        "French Guiana",
        "Guyana",
        "Paraguay",
        "Peru",
        "Suriname",
        "Uruguay",
        "Venezuela"
      ),
      "Bahamas" -> List(),
      "Bhutan" -> List(
        "China",
        "India"
      ),
      "Bouvet Island" -> List(), //dont have data on
      "Botswana" -> List(
        "Namibia",
        "South Africa",
        "Zambia",
        "Zimbabwe"
      ),
      "Belarus" -> List(
        "Latvia",
        "Lithuania",
        "Poland",
        "Russia",
        "Ukraine"
      ),
      "Belize" -> List(
        "Guatemala",
        "Mexico"
      ),
      "Canada" -> List(
        "United States"
      ),
      "Cocos (Keeling) Islands" -> List(),
      "Democratic Republic of Congo" -> List(
        "Angola",
        "Burundi",
        "Central African Republic",
        "Congo",
        "Rwanda",
        "South Sudan",
        "Tanzania",
        "Uganda",
        "Zambia"
      ),
      "Central African Republic" -> List(
        "Cameroon",
        "Chad",
        "Congo",
        "Congo",
        "South Sudan",
        "Sudan"
      ),
      "Congo" -> List(
        "Angola",
        "Cameroon",
        "Central African Republic",
        "Democratic Republic of Congo",
        "Gabon"
      ),
      "Switzerland" -> List(
        "Austria",
        "France",
        "Germany",
        "Italy",
        "Liechtenstein"
      ),
      "Cote d'Ivoire" -> List( //correct spelling
        "Burkina Faso",
        "Ghana",
        "Guinea",
        "Liberia",
        "Mali"
      ),
      "Cook Islands" -> List(),
      "Chile" -> List(
        "Argentina",
        "Bolivia",
        "Peru"
      ),
      "Cameroon" -> List(
        "Central African Republic",
        "Chad",
        "Congo",
        "Equatorial Guinea",
        "Gabon",
        "Nigeria"
      ),
      "China" -> List(
        "Afghanistan",
        "Bhutan",
        "Hong Kong",
        "India",
        "Kazakhstan",
        "North Korea",
        "Kyrgyzstan",
        "Laos",
        "Macao",
        "Mongolia",
        "Myanmar",
        "Nepal",
        "Pakistan",
        "Russia",
        "Tajikistan",
        "Vietnam"
      ),
      "Colombia" -> List(
        "Brazil",
        "Ecuador",
        "Panama",
        "Peru",
        "Venezuela"
      ),
      "Costa Rica" -> List(
        "Nicaragua",
        "Panama"
      ),
      "Cuba" -> List(),
      "Cabo Verde" -> List(), //dont have data on
      "Curacao" -> List(), //Curcacao
      "Christmas Island" -> List(),
      "Czechia" -> List(
        "Austria",
        "Germany",
        "Poland",
        "Slovakia"
      ),
      "Germany" -> List(
        "Austria",
        "Belgium",
        "Czechia",
        "Denmark",
        "France",
        "Luxembourg",
        "Netherlands",
        "Poland",
        "Switzerland"
      ),
      "Djibouti" -> List(
        "Eritrea",
        "Ethiopia",
        "Somalia"
      ),
      "Denmark" -> List(
        "Germany"
      ),
      "Dominica" -> List(),
      "Dominican Republic" -> List(
        "Haiti"
      ),
      "Algeria" -> List(
        "Libya",
        "Mali",
        "Mauritania",
        "Morocco",
        "Niger",  //correct spelling
        "Tunisia",
        "Western Sahara"
      ),
      "Ecuador" -> List(
        "Colombia",
        "Peru"
      ),
      "Estonia" -> List(
        "Latvia",
        "Russia"
      ),
      "Egypt" -> List(
        "Israel",
        "Libya",
        "Palestine",
        "Sudan"
      ),
      "Western Sahara" -> List(
        "Algeria",
        "Mauritania",
        "Morocco"
      ),
      "Eritrea" -> List(
        "Djibouti",
        "Ethiopia",
        "Sudan"
      ),
      "Spain" -> List(
        "Andorra",
        "France",
        "Gibraltar",
        "Morocco",
        "Portugal"
      ),
      "Ethiopia" -> List(
        "Djibouti",
        "Eritrea",
        "Kenya",
        "Somalia",
        "South Sudan",
        "Sudan"
      ),
      "Finland" -> List(
        "Norway",
        "Russia",
        "Sweden"
      ),
      "Fiji" -> List(),
      "Falkland Islands" -> List(),
      "Federated States of Micronesia" -> List(),
      "Faeroe Island" -> List(), //Faroe
      "France" -> List(
        "Andorra",
        "Belgium",
        "Germany",
        "Italy",
        "Luxembourg",
        "Monaco",
        "Spain",
        "Switzerland"
      ),
      "Gabon" -> List(
        "Cameroon",
        "Congo",
        "Equatorial Guinea"
      ),
      "United Kingdom" -> List(
        "France",
        "Ireland"
      ),
      "Grenada" -> List(),
      "Georgia" -> List(
        "Armenia",
        "Azerbaijan",
        "Russia",
        "Turkey"
      ),
      "French Guiana" -> List(
        "Brazil",
        "Suriname"
      ),
      "Guernsey" -> List(),
      "Ghana" -> List(
        "Burkina Faso",
        "Cote d’Ivoire",  //correct spelling
        "Togo"
      ),
      "Gibraltar" -> List(
        "Spain"
      ),
      "Greenland" -> List(),
      "Gambia" -> List(
        "Senegal"
      ),
      "Guinea" -> List(
        "Cote d’Ivoire", //correct spelling
        "Guinea-Bissau",
        "Liberia",
        "Mali",
        "Senegal",
        "Sierra Leone"
      ),
      "Guadeloupe" -> List(),
      "Equatorial Guinea" -> List(
        "Cameroon",
        "Gabon"
      ),
      "Greece" -> List(
        "Albania",
        "Bulgaria",
        "Macedonia",
        "Turkey"
      ),
      "Guatemala" -> List(
        "Belize",
        "El Salvador",
        "Honduras",
        "Mexico"
      ),
      "Guam" -> List(),
      "Guinea-Bissau" -> List(
        "Guinea",
        "Senegal"
      ),
      "Guyana" -> List(
        "Brazil",
        "Suriname",
        "Venezuela"
      ),
      "Hong Kong" -> List(
        "China"
      ),
      "Honduras" -> List(
        "El Salvador",
        "Guatemala",
        "Nicaragua"
      ),
      "Croatia" -> List(
        "Bosnia and Herzegovina",
        "Hungary",
        "Montenegro",
        "Serbia",
        "Slovenia"
      ),
      "Haiti" -> List(
        "Dominican Republic"
      ),
      "Hungary" -> List(
        "Austria",
        "Croatia",
        "Romania",
        "Serbia",
        "Slovakia",
        "Slovenia",
        "Ukraine"
      ),
      "Indonesia" -> List(
        "Malaysia",
        "Papua New Guinea",
        "Timor-Leste"
      ),
      "Ireland" -> List(
        "United Kingdom"
      ),
      "Israel" -> List(
        "Egypt",
        "Jordan",
        "Lebanon",
        "Palestine",
        "Syria"
      ),
      "Isle of Man" -> List(),
      "India" -> List(
        "Bangladesh",
        "Bhutan",
        "China",
        "Myanmar",
        "Nepal",
        "Pakistan"
      ),
      "British Indian Ocean Territory" -> List(),
      "Iraq" -> List(
        "Iran",
        "Jordan",
        "Kuwait",
        "Saudi Arabia",
        "Syria",
        "Turkey"
      ),
      "Iran" -> List(
        "Afghanistan",
        "Armenia",
        "Azerbaijan",
        "Iraq",
        "Pakistan",
        "Turkey",
        "Turkmenistan"
      ),
      "Iceland" -> List(),
      "Italy" -> List(
        "Austria",
        "France",
        "San Marino",
        "Slovenia",
        "Switzerland",
        "Holy See"
      ),
      "Jersey" -> List(),
      "Jamaica" -> List(),
      "Jordan" -> List(
        "Iraq",
        "Israel",
        "Palestine",
        "Saudi Arabia",
        "Syria"
      ),
      "Japan" -> List(),
      "Kenya" -> List(
        "Ethiopia",
        "Somalia",
        "South Sudan",
        "Tanzania",
        "Uganda"
      ),
      "Kyrgyzstan" -> List(
        "China",
        "Kazakhstan",
        "Tajikistan",
        "Uzbekistan"
      ),
      "Cambodia" -> List(
        "Laos",
        "Thailand",
        "Vietnam"
      ),
      "Kiribati" -> List(),
      "Comoros" -> List(), //Comorros
      "Saint Kitts and Nevis" -> List(),
      "North Korea" -> List(
        "China",
        "South Korea",
        "Russia"
      ),
      "South Korea" -> List(
        "North Korea"
      ),
      "Kuwait" -> List(
        "Iraq",
        "Saudi Arabia"
      ),
      "Cayman Island" -> List(),
      "Kazakhstan" -> List(
        "China",
        "Kyrgyzstan",
        "Russia",
        "Turkmenistan",
        "Uzbekistan"
      ),
      "Laos" -> List(
        "China",
        "Cambodia",
        "Myanmar",
        "Thailand",
        "Vietnam"
      ),
      "Lebanon" -> List(
        "Israel",
        "Syria"
      ),
      "Saint Lucia" -> List(),
      "Liechtenstein" -> List(  //liechtenstien
        "Austria",
        "Switzerland"
      ),
      "Sri Lanka" -> List(),
      "Liberia" -> List(
        "Cote d’Ivoire", //correct spelling
        "Guinea",
        "Sierra Leone"
      ),
      "Lesotho" -> List(
        "South Africa"
      ),
      "Lithuania" -> List(
        "Belarus",
        "Latvia",
        "Poland",
        "Russia"
      ),
      "Luxembourg" -> List(
        "Belarus",
        "Latvia",
        "Poland",
        "Russia"
      ),
      "Latvia" -> List(
        "Belarus",
        "Estonia",
        "Lithuania",
        "Russia"
      ),
      "Libya" -> List(
        "Algeria",
        "Chad",
        "Egypt",
        "Niger", //correct spelling
        "Sudan",
        "Tunisia"
      ),
      "Morocco" -> List(
        "Algeria",
        "Spain",
        "Western Sahara"
      ),
      "Monaco" -> List(
        "France"
      ),
      "Moldova" -> List(
        "Romania",
        "Ukraine"
      ),
      "Montenegro" -> List(
        "Albania",
        "Bosnia and Herzegovina",
        "Croatia",
        "Serbia"
      ),
      "Madagascar" -> List(),
      "Marshall Islands" -> List(),
      "Macedonia" -> List(
        "Albania",
        "Bulgaria",
        "Greece",
        "Serbia"
      ),
      "Mali" -> List(
        "Algeria",
        "Burkina Faso",
        "Cote d’Ivoire", //correct spelling
        "Guinea",
        "Mauritania",
        "Niger",   // correct spelling
        "Senegal"
      ),
      "Myanmar" -> List(
        "Bangladesh",
        "China",
        "India",
        "Laos",
        "Thailand"
      ),
      "Mongolia" -> List(
        "China",
        "Russia"
      ),
      "Macao" -> List(
        "China"
      ),
      "Northern Mariana Islands" -> List(),
      "Martinique" -> List(),
      "Mauritania" -> List(
        "Algeria",
        "Mali",
        "Senegal",
        "Western Sahara"
      ),
      "Montserrat" -> List(),
      "Malta" -> List(),
      "Mauritius" -> List(),
      "Maldives" -> List(),
      "Malawi" -> List(
        "Mozambique",
        "Tanzania",
        "Zambia"
      ),
      "Mexico" -> List(
        "Belize",
        "Guatemala",
        "United States"
      ),
      "Malaysia" -> List(
        "Brunei",
        "Indonesia",
        "Thailand"
      ),
      "Mozambique" -> List(
        "Malawi",
        "Eswatini",
        "South Africa",
        "Tanzania",
        "Zambia",
        "Zimbabwe"
      ),
      "Namibia" -> List(
        "Angola",
        "Botswana",
        "South Africa",
        "Zambia"
      ),
      "New Caledonia" -> List(), //New Caldonia
      "Niger" -> List( //correct spelling
        "Algeria",
        "Benin",
        "Burkina Faso",
        "Chad",
        "Libya",
        "Mali",
        "Nigeria"
      ),
      "Norfolk Island" -> List(),
      "Nigeria" -> List(
        "Benin",
        "Cameroon",
        "Chad",
        "Niger" //correct spelling
      ),
      "Nicaragua" -> List(
        "Costa Rica",
        "Honduras"
      ),
      "Netherlands" -> List(
        "Belgium",
        "Germany"
      ),
      "Norway" -> List(
        "Finland",
        "Russia",
        "Sweden"
      ),
      "Nepal" -> List(
        "China",
        "India"
      ),
      "Nauru" -> List(),
      "Niue" -> List(), //dont have any data for
      "New Zealand" -> List(),
      "Oman" -> List(
        "United Arab Emirates",
        "Saudi Arabia",
        "Yemen"
      ),
      "Panama" -> List(
        "Colombia",
        "Costa Rica"
      ),
      "Peru" -> List(
        "Bolivia",
        "Brazil",
        "Chile",
        "Colombia",
        "Ecuador"
      ),
      "French Polynesia" -> List(),
      "Papua New Guinea" -> List(
        "Indonesia"
      ),
      "Philippines" -> List(),
      "Pakistan" -> List(
        "Afghanistan",
        "China",
        "India",
        "Iran"
      ),
      "Poland" -> List(
        "Belarus",
        "Czechia",
        "Germany",
        "Lithuania",
        "Russia",
        "Slovakia",
        "Ukraine"
      ),
      "Saint Pierre and Miquelon" -> List(),
      "Pitcairn" -> List(),
      "Puerto Rico" -> List(),
      "Palestine" -> List(
        "Egypt",
        "Israel",
        "Jordan"
      ),
      "Portugal" -> List(
        "Spain"
      ),
      "Palau" -> List(), //dont have any data for
      "Paraguay" -> List(
        "Argentina",
        "Bolivia",
        "Brazil"
      ),
      "Qatar" -> List(
        "Saudi Arabia"
      ),
      "Romania" -> List(
        "Bulgaria",
        "Hungary",
        "Moldova",
        "Serbia",
        "Ukraine"
      ),
      "Serbia" -> List(
        "Albania",
        "Bosnia and Herzegovina",
        "Bulgaria",
        "Croatia",
        "Hungary",
        "Montenegro",
        "Macedonia",
        "Romania"
      ),
      "Russia" -> List(
        "Azerbaijan",
        "Belarus",
        "China",
        "Estonia",
        "Finland",
        "Georgia",
        "Kazakhstan",
        "North Korea",
        "Latvia",
        "Lithuania",
        "Mongolia",
        "Norway",
        "Poland",
        "Ukraine"
      ),
      "Rwanda" -> List(
        "Burundi",
        "Congo",
        "Tanzania",
        "Uganda"
      ),
      "Saudi Arabia" -> List(
        "Iraq",
        "Jordan",
        "Kuwait",
        "Oman",
        "Qatar",
        "United Arab Emirates",
        "Yemen"
      ),
      "Solomon Islands" -> List(),
      "Seychelles" -> List(),
      "Sudan" -> List(
        "Central African Republic",
        "Chad",
        "Egypt",
        "Ethiopia",
        "Eritrea",
        "Libya",
        "South Sudan"
      ),
      "Sweden" -> List(
        "Finland",
        "Norway"
      ),
      "Singapore" -> List(),
      "Saint Helena" -> List(),
      "Slovenia" -> List(
        "Austria",
        "Croatia",
        "Hungary",
        "Italy"
      ),
      "Svalbard and Jan Mayen" -> List(), //dont have any data for
      "Slovakia" -> List(
        "Austria",
        "Czechia",
        "Hungary",
        "Poland",
        "Ukraine"
      ),
      "Sierra Leone" -> List(
        "Guinea",
        "Liberia"
      ),
      "San Marino" -> List(
        "Italy"
      ),
      "Senegal" -> List(
        "Gambia",
        "Guinea",
        "Guinea-Bissau",
        "Mali",
        "Mauritania"
      ),
      "Somalia" -> List(
        "Djibouti",
        "Ethiopia",
        "Kenya"
      ),
      "Suriname" -> List(
        "Brazil",
        "French Guiana",
        "Guyana"
      ),
      "South Sudan" -> List(
        "Central African Republic",
        "Congo",
        "Ethiopia",
        "Kenya",
        "Sudan",
        "Uganda",
      ),
      "Sao Tome and Principe" -> List(),
      "El Salvador" -> List(
        "Guatemala",
        "Honduras"
      ),

      "Sint Maarten (Dutch part)" -> List(), //Sint Maartin
      "Syria" -> List(
        "Iraq",
        "Israel",
        "Jordan",
        "Lebanon",
        "Turkey"
      ),
      "Eswatini" -> List(
        "Mozambique",
        "South Africa"
      ),
      "Turks and Caicos Islands" -> List(), //correct spelling
      "Chad" -> List(
        "Cameroon",
        "Central African Republic",
        "Libya",
        "Niger",  //correct spelling
        "Nigeria",
        "Sudan"
      ),
      "Togo" -> List(
        "Benin",
        "Burkina Faso",
        "Ghana"
      ),
      "Thailand" -> List(
        "Cambodia",
        "Laos",
        "Malaysia",
        "Myanmar"
      ),
      "Tajikistan" -> List(
        "Afghanistan",
        "China",
        "Kyrgyzstan",
        "Uzbekistan"
      ),
      "Tokelau" -> List(), //dont have data for
      "Timor-Leste" -> List(),
      "Turkmenistan" -> List(
        "Indonesia",
        "Afghanistan",
        "Iran",
        "Kazakhstan",
        "Uzbekistan"
      ),
      "Tunisia" -> List(
        "Algeria",
        "Libya"
      ),
      "Tonga" -> List(),
      "Turkey" -> List(
        "Armenia",
        "Azerbaijan",
        "Bulgaria",
        "Georgia",
        "Greece",
        "Iran",
        "Iraq",
        "Syria"
      ),
      "Trinidad and Tobago" -> List(),
      "Tuvalu" -> List(),
      "Taiwan" -> List(),
      "Tanzania" -> List(
        "Burundi",
        "Congo",
        "Kenya",
        "Malawi",
        "Mozambique",
        "Rwanda",
        "Uganda",
        "Zambia"
      ),
      "Ukraine" -> List(
        "Belarus",
        "Hungary",
        "Moldova",
        "Poland",
        "Romania",
        "Russia",
        "Slovakia"
      ),
      "Uganda" -> List(
        "Congo",
        "Kenya",
        "Rwanda",
        "South Sudan",
        "Tanzania"
      ),
      "United States" -> List(
        "Canada",
        "Mexico"
      ),
      "Uruguay" -> List(
        "Argentina",
        "Brazil"
      ),
      "Uzbekistan" -> List(
        "Afghanistan",
        "Kazakhstan",
        "Kyrgyzstan",
        "Tajikistan",
        "Turkmenistan"
      ),
      "Holy See" -> List(
        "Italy"
      ),
      "Venezuela" -> List(
        "Brazil",
        "Colombia",
        "Guyana"
      ),
      "Virgin Islands" -> List(),
      "Vietnam" -> List(
        "Cambodia",
        "China",
        "Laos"
      ),
      "Vanuatu" -> List(),
      "Samoa" -> List(),
      "Yemen" -> List(
        "Oman",
        "Saudi Arabia"
      ),
      "South Africa" -> List(
        "Botswana",
        "Lesotho",
        "Mozambique",
        "Namibia",
        "Eswatini",
        "Zimbabwe"
      ),
      "Zambia" -> List(
        "Angola",
        "Botswana",
        "Congo",
        "Malawi",
        "Mozambique",
        "Namibia",
        "Tanzania",
        "Zimbabwe"
      ),
      "Zimbabwe" -> List(
        "Botswana",
        "Mozambique",
        "South Africa",
        "Zambia"
      )
    )
    borders_dictionary
  }
}

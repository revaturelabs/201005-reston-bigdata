package RedTeam

import org.apache.spark.sql.{SparkSession}
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import org.apache.spark.sql.functions.{bround, count, desc, when}

object InfectionRates {
    def redq6(spark: SparkSession): Unit = {


        spark.sparkContext.setLogLevel("ERROR")

        // The following is required in order for this to work properly
        import spark.implicits._

        //Prints batch time for each batch that runs
        runIncreaseRate()

        def runIncreaseRate(): Unit = {

            val Africa = Array("Algeria", "Angola",
                "Benin", "Botswana", "Burkina Faso", "Burundi",
                "Cameroon", "Cabo Verde", "Central African Republic",
                "Chad", "Comoros", "Côte d'Ivoire",
                "DRC", "Djibouti", "Egypt",
                "Equatorial Guinea", "Eritrea",
                "Ethiopia", "Gabon", "Gambia", "Ghana", "Guinea",
                "Guinea-Bissau", "Kenya", "Lesotho", "Liberia", "Libyan Arab Jamahiriya",
                "Madagascar", "Malawi", "Mali", "Mauritania", "Mauritius", "Mayotte", "Morocco",
                "Mozambique", "Namibia", "Niger", "Nigeria", "Republic of the Congo",
                "Reunion", "Rwanda", "Réunion", "Saint Helena", "Sao Tome and Principe",
                "Senegal", "Seychelles", "Sierra Leone", "Somalia", "South Africa",
                "South Sudan", "Sudan", "Swaziland", "Tanzania", "Togo",
                "Tunisia", "Uganda", "Western Sahara", "Zambia", "Zimbabwe").toSeq

            val Asia = Array("Afghanistan", "Armenia",
                "Azerbaijan", "Bahrain", "Bangladesh", "Bhutan", "Brunei",
                "Myanmar", "Cambodia", "China", "Cyprus",
                "Timor-Leste", "Georgia", "Hong Kong", "India", "Indonesia",
                "Iran", "Iraq", "Israel", "Japan",
                "Jordan", "Kazakhstan", "Kuwait", "Kyrgyzstan", "Lao People's Democratic Republic",
                "Lebanon", "Macao", "Malaysia",
                "Maldives", "Mongolia", "Nepal", "North Korea", "Oman",
                "Pakistan", "Palestine", "Philippines", "Qatar", "Saudi Arabia", "Singapore",
                "S. Korea", "Sri Lanka", "Syrian Arab Republic",
                "Taiwan", "Tajikistan", "Thailand", "Turkey",
                "Turkmenistan", "UAE",
                "Uzbekistan", "Vietnam", "Yemen").toSeq

            val Europe = Array("Albania", "Andorra", "Austria", "Belarus",
                "Belgium", "Bosnia", "Bulgaria", "Channel Islands", "Croatia",
                "Czechia", "Denmark", "Estonia", "Faroe Islands", "Finland",
                "France", "Germany", "Gibraltar", "Greece",
                "Holy See (Vatican City State)", "Hungary", "Iceland", "Ireland",
                "Italy", "Isle of Man", "Kosovo", "Latvia", "Liechtenstein",
                "Lithuania", "Luxembourg", "Macedonia", "Malta",
                "Moldova", "Monaco", "Montenegro", "Netherlands",
                "Norway", "Poland", "Portugal", "Romania", "Russia",
                "San Marino", "Slovakia", "Slovenia", "Spain", "Serbia",
                "Sweden", "Switzerland", "Ukraine", "UK").toSeq

            val Caribbean = Array("Anguilla", "Antigua and Barbuda", "Aruba",
                "Bahamas", "Barbados", "Bermuda", "British Virgin Islands", "Caribbean Netherlands", "Cayman Islands",
                "Cuba", "Curaçao", "Dominica", "Dominican Republic", "Grenada", "Guadeloupe", "Haiti",
                "Jamaica", "Martinique", "Montserrat", "Netherlands Antilles", "Puerto Rico", "St. Barth", "Saint Martin",
                "Saint Kitts and Nevis", "Saint Lucia", "Saint Vincent and the Grenadines", "Sint Maarten",
                "Trinidad and Tobago", "Turks and Caicos Islands", "U.S. Virgin Islands"
            )

            val Central_America = Array("Belize", "Costa Rica",
                "El Salvador",
                "Guatemala", "Honduras",
                "Nicaragua", "Panama"
            )

            val North_America = Array("Canada",
                "Greenland", "Mexico",
                "Saint Pierre Miquelon", "USA"
            )

            val South_America = Array("Argentina", "Bolivia",
                "Brazil", "Chile", "Colombia", "Ecuador",
                "Falkland Islands (Malvinas)", "French Guiana", "Guyana", "Paraguay",
                "Peru", "Suriname", "Uruguay", "Venezuela"
            )

            val Oceania = Array("American Samoa",
                "Australia", "Christmas Island", "Cocos (Keeling) Islands",
                "Cook Islands", "Federated States of Micronesia", "Fiji",
                "French Polynesia", "Guam", "Kiribati", "Marshall Islands",
                "Nauru", "New Caledonia", "New Zealand",
                "Niue", "Northern Mariana Islands", "Palau",
                "Papua New Guinea", "Pitcairn Islands",
                "Samoa", "Solomon Islands", "Tokelau", "Tonga",
                "Tuvalu", "Vanuatu", "Wallis and Futuna"
            )


            val todayJson = spark.read.option("true", "multiline").json("s3a://adam-king-848/data/today.json")
            val today = todayJson.withColumn("Region",when($"country".isin(Africa: _*), "Africa")
                .when($"country".isin(Asia: _*), "Asia")
                .when($"country".isin(Europe: _*), "Europe")
                .when($"country".isin(Caribbean: _*), "Caribbean")
                .when($"country".isin(Central_America: _*), "Central America")
                .when($"country".isin(North_America: _*), "North America")
                .when($"country".isin(South_America: _*), "South America")
                .when($"country".isin(Oceania: _*), "Oceania"))
            today.createOrReplaceTempView("today")

            val yesterdayTemp = spark.read.option("true", "multiline").json("s3a://adam-king-848/data/yesterday.json")
            val yesterday = yesterdayTemp.withColumn("Region",when($"country".isin(Africa: _*), "Africa")
                .when($"country".isin(Asia: _*), "Asia")
                .when($"country".isin(Europe: _*), "Europe")
                .when($"country".isin(Caribbean: _*), "Caribbean")
                .when($"country".isin(Central_America: _*), "Central America")
                .when($"country".isin(North_America: _*), "North America")
                .when($"country".isin(South_America: _*), "South America")
                .when($"country".isin(Oceania: _*), "Oceania"))
            yesterday.createOrReplaceTempView("yesterday")

            //Prints the time the application ran
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val x = LocalDateTime.now().format(formatter)
            println(s"Stats as of: ${x}")
            println("================================")
            println()

            //Calculating Regional Infection Rate Changes
            println("Regions and their change in Infection Rate")
            val dfAfrica = spark.sql("Select first(yesterday.Region) As Region, " +
                "bround(avg((((today.todayCases/today.population)*1000000) - ((yesterday.todayCases/yesterday.population)*1000000)) / yesterday.casesPerOneMillion * 100), 2) " +
                "as Infection_Rate_Change FROM yesterday INNER JOIN" +
                " today ON today.country=yesterday.country" +
                " WHERE yesterday.Region='Africa'")
            val dfAsia = spark.sql("Select first(yesterday.Region) As Region, " +
                "bround(avg((((today.todayCases/today.population)*1000000) - ((yesterday.todayCases/yesterday.population)*1000000)) / yesterday.casesPerOneMillion * 100), 2) " +
                "as Infection_Rate_Change FROM yesterday INNER JOIN" +
                " today ON today.country=yesterday.country" +
                " WHERE yesterday.Region='Asia'")
            val dfCaribbean= spark.sql("Select first(yesterday.Region) As Region, " +
                "bround(avg((((today.todayCases/today.population)*1000000) - ((yesterday.todayCases/yesterday.population)*1000000)) / yesterday.casesPerOneMillion * 100), 2) " +
                "as Infection_Rate_Change FROM yesterday INNER JOIN" +
                " today ON today.country=yesterday.country" +
                " WHERE yesterday.Region='Caribbean'")
            val dfCentralAmerica = spark.sql("Select first(yesterday.Region) As Region, " +
                "bround(avg((((today.todayCases/today.population)*1000000) - ((yesterday.todayCases/yesterday.population)*1000000)) / yesterday.casesPerOneMillion * 100), 2) " +
                "as Infection_Rate_Change FROM yesterday INNER JOIN" +
                " today ON today.country=yesterday.country" +
                " WHERE yesterday.Region='Central America'")
            val dfEurope= spark.sql("Select first(yesterday.Region) As Region, " +
                "bround(avg((((today.todayCases/today.population)*1000000) - ((yesterday.todayCases/yesterday.population)*1000000)) / yesterday.casesPerOneMillion * 100), 2) " +
                "as Infection_Rate_Change FROM yesterday INNER JOIN" +
                " today ON today.country=yesterday.country" +
                " WHERE yesterday.Region='Europe'")
            val dfNorthAmerica = spark.sql("Select first(yesterday.Region) As Region, " +
                "bround(avg((((today.todayCases/today.population)*1000000) - ((yesterday.todayCases/yesterday.population)*1000000)) / yesterday.casesPerOneMillion * 100), 2) " +
                "as Infection_Rate_Change FROM yesterday INNER JOIN" +
                " today ON today.country=yesterday.country" +
                " WHERE yesterday.Region='North America'")
            val dfOceania = spark.sql("Select first(yesterday.Region) As Region, " +
                "bround(avg((((today.todayCases/today.population)*1000000) - ((yesterday.todayCases/yesterday.population)*1000000)) / yesterday.casesPerOneMillion * 100), 2) " +
                "as Infection_Rate_Change FROM yesterday INNER JOIN" +
                " today ON today.country=yesterday.country" +
                " WHERE yesterday.Region='Oceania'")
            val dfSouthAmerica = spark.sql("Select first(yesterday.Region) As Region, " +
                "bround(avg((((today.todayCases/today.population)*1000000) - ((yesterday.todayCases/yesterday.population)*1000000)) / yesterday.casesPerOneMillion * 100), 2) " +
                "as Infection_Rate_Change FROM yesterday INNER JOIN" +
                " today ON today.country=yesterday.country" +
                " WHERE yesterday.Region='South America'")
            val regions = dfAfrica.union(dfAsia)
                .union(dfCaribbean)
                .union(dfCentralAmerica)
                .union(dfEurope)
                .union(dfNorthAmerica)
                .union(dfOceania)
                .union(dfSouthAmerica)
            regions
                .sort(desc("Infection_Rate_Change"))
                .show()

            //Percentage of Countries with Increasing Infection Rate
            println("Percentage of countries with a rising infection rate")
            val risingRatesTemp = spark.sql("SELECT today.country AS Country," +
                "bround((((today.todayCases/today.population)*1000000) - ((yesterday.todayCases/yesterday.population)*1000000)) / yesterday.casesPerOneMillion * 100, 2) " +
                "as Infection_Rate_Change FROM today INNER JOIN" +
                " yesterday ON today.country=yesterday.country")

            val risingRates = risingRatesTemp.filter($"Infection_Rate_Change" > 0)
                .select(bround((count("Infection_Rate_Change") / 218) * 100, 2) as "Percentage of Countries w/Rising Infection Rate")
            risingRates.show()

            // Country - most and least increase in infection rate per capita
            println("Country with the LARGEST increase in Infection Rate")
            spark.sql("SELECT today.country AS Country, today.Region AS Region," +
                " bround((((today.todayCases/today.population)*1000000) - ((yesterday.todayCases/yesterday.population)*1000000)) / yesterday.casesPerOneMillion * 100, 2) " +
                "as Infection_Rate_Change FROM today INNER JOIN" +
                " yesterday ON today.country=yesterday.country ORDER BY Infection_Rate_Change DESC LIMIT 1").show(false)
            println("Country with the SMALLEST increase and/or LARGEST decrease in Infection Rate")
            spark.sql("SELECT today.country AS Country, today.Region AS Region," +
                " bround((((today.todayCases/today.population)*1000000) - ((yesterday.todayCases/yesterday.population)*1000000)) / yesterday.casesPerOneMillion * 100, 2) " +
                "as Infection_Rate_Change FROM today INNER JOIN" +
                " yesterday ON today.country=yesterday.country ORDER BY Infection_Rate_Change ASC NULLS LAST LIMIT 1").show(false)

            // Country - most and least increase in fatality rate per capita
            println("Country with the LARGEST increase in Fatality Rate")
            spark.sql("SELECT today.country AS Country, today.Region AS Region," +
                " bround((((today.todayDeaths/today.population)*1000000) - ((yesterday.todayDeaths/yesterday.population)*1000000)) / yesterday.deathsPerOneMillion * 100, 2) " +
                "as Fatality_Rate_Change FROM today INNER JOIN" +
                " yesterday ON today.country=yesterday.country ORDER BY Fatality_Rate_Change DESC LIMIT 1").show(false)
            println("Country with the SMALLEST increase and/or LARGEST decrease in Fatality Rate")
            spark.sql("SELECT today.country AS Country, today.Region AS Region," +
                " bround((((today.todayDeaths/today.population)*1000000) - ((yesterday.todayDeaths/yesterday.population)*1000000)) / yesterday.deathsPerOneMillion * 100, 2) " +
                "as Fatality_Rate_Change FROM today INNER JOIN" +
                " yesterday ON today.country=yesterday.country ORDER BY Fatality_Rate_Change ASC NULLS LAST LIMIT 1").show(false)

            // Country - most and least increase in recovery rate per capita
            println("Country with the LARGEST increase in Recovery Rate")
            spark.sql("SELECT today.country AS Country, today.Region AS Region," +
                " bround((((today.todayRecovered/today.population)*1000000) - ((yesterday.todayRecovered/yesterday.population)*1000000)) / yesterday.recoveredPerOneMillion * 100, 2) " +
                "as Recovery_Rate_Change FROM today INNER JOIN" +
                " yesterday ON today.country=yesterday.country ORDER BY Recovery_Rate_Change DESC LIMIT 1").show(false)
            println("Country with the SMALLEST increase and/or LARGEST decrease in Recovery Rate")
            spark.sql("SELECT today.country AS Country, today.Region AS Region," +
                " bround((((today.todayRecovered/today.population)*1000000) - ((yesterday.todayRecovered/yesterday.population)*1000000)) / yesterday.recoveredPerOneMillion * 100, 2) " +
                "as Recovery_Rate_Change FROM today INNER JOIN" +
                " yesterday ON today.country=yesterday.country ORDER BY Recovery_Rate_Change ASC NULLS LAST LIMIT 1").show(false)
        }
    }
}
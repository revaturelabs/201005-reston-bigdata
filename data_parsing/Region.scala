package data_parsing

case class Region(
                   name: String = null,
                   var countries: List[String] = List(),
                   var agg_population: Long = 0,
                   var avg_population_density: Double = 0,
                   var avg_median_age: Double = 0,
                   var avg_aged_65_older: Double = 0,
                   var avg_aged_70_older: Double = 0,
                   var avg_gdp_per_capita: Double = 0,
                   var real_gdp_per_capita: Double = 0,
                   var avg_cardiovasc_death_rate: Double = 0,
                   var avg_diabetes_prevalence: Double = 0,
                   var avg_handwashing_facilities: Double = 0,
                   var avg_hospital_beds_per_thousand: Double = 0,
                   var avg_life_expectancy: Double = 0,
                   var avg_human_development_index: Double = 0,
                   var agg_gdp: Long = 0,
                   var agg_cases: Int = 0,
                   var agg_recoveries: Int = 0,
                   var agg_deaths: Int = 0,
                   var avg_unemployment: Double = 0
                 ) {

  def initialize(): Unit = {
    countries = regionCountries(name)
    for(patrium <- countries) {
      val country: Country = getCountry(patrium)
      agg_population += country.population
      avg_population_density += country.population_density
      avg_median_age += country.median_age
      avg_aged_65_older += country.aged_65_older
      avg_aged_70_older += country.aged_70_older
      avg_gdp_per_capita += country.gdp_per_capita
      avg_cardiovasc_death_rate += country.cardiovasc_death_rate
      avg_diabetes_prevalence += country.diabetes_prevalence
      avg_handwashing_facilities += country.handwashing_facilities
      avg_hospital_beds_per_thousand += country.hospital_beds_per_thousand
      avg_life_expectancy += country.life_expectancy
      avg_human_development_index += country.human_development_index
      agg_gdp += country.gdp
      avg_unemployment += country.unemployment
    }
    avg_population_density = avg_population_density/countries.length
    avg_median_age = avg_median_age/countries.length
    avg_aged_65_older = avg_aged_65_older/countries.length
    avg_aged_70_older = avg_aged_70_older/countries.length
    avg_gdp_per_capita = avg_gdp_per_capita/countries.length
    avg_cardiovasc_death_rate = avg_cardiovasc_death_rate/countries.length
    avg_diabetes_prevalence = avg_diabetes_prevalence/countries.length
    avg_handwashing_facilities = avg_handwashing_facilities/countries.length
    avg_hospital_beds_per_thousand = avg_hospital_beds_per_thousand/countries.length
    avg_life_expectancy = avg_life_expectancy/countries.length
    avg_human_development_index = avg_human_development_index/countries.length
    avg_unemployment = avg_unemployment/countries.length
  }

  def fullUpdate(): Unit = {
    updateCases()
    updateDeaths()
    updateRecoveries()
  }

  def getCountry(name: String): Country = {
    countriesDF.select("name" == name).as[Country]
  }

  def updateCases(): Unit = {
    var agg_cases = 0
    for(country <- countries)
      agg_cases += getCountry(country).total_cases
  }

  def updateDeaths(): Unit = {
    var agg_deaths = 0
    for (country <- countries)
      agg_deaths += getCountry(country).deaths
  }

  def updateRecoveries(): Unit = {
    var agg_recoveries = 0
    for (country <- countries)
      agg_recoveries = getCountry(country).recoveries
  }

}

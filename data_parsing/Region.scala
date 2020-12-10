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
                   var agg_deaths: Int = 0
                 )

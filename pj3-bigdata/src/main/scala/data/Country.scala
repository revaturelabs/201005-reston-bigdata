package data_parsing

case class Country(
                    name: String = null,
                    population: Long = 0,
                    population_density: Double = 0,
                    median_age: Double = 0,
                    aged_65_older: Double = 0,
                    aged_70_older: Double = 0,
                    gdp_per_capita: Double = 0,
                    cardiovasc_death_rate: Double = 0,
                    diabetes_prevalence: Double = 0,
                    handwashing_facilities: Double = 0,
                    hospital_beds_per_thousand: Double = 0,
                    life_expectancy: Double = 0,
                    human_development_index: Double = 0,
                    gdp: Long = 0,
                    total_cases: Int = 0,
                    recoveries: Int = 0,
                    deaths: Int = 0,
                    unemployment: Double = 0
                  ) {

}

package data_parsing

import java.util.Date

case class cData(
                  country: String = null,
                  date: Date = null,
                  total_cases: Int = 0,
                  new_cases: Int = 0,
                  new_cases_smoothed: Double = 0,
                  total_deaths: Int = 0,
                  new_deaths: Int = 0,
                  new_deaths_smoothed: Double = 0,
                  total_cases_per_million: Double = 0,
                  new_cases_per_million: Double = 0,
                  new_cases_smoothed_per_million: Double = 0,
                  total_deaths_per_million: Double = 0,
                  new_deaths_per_million: Double = 0,
                  new_deaths_smoothed_per_million: Double = 0,
                  total_tests: Int = 0,
                  new_tests: Int = 0,
                  total_tests_per_thousand: Double = 0,
                  new_tests_per_thousand: Double = 0,
                  new_tests_smoothed: Double = 0,
                  new_tests_smoothed_per_thousand: Double = 0,
                  tests_units: String = null
                ) {}

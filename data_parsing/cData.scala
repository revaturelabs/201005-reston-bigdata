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
                  reproduction_rate: Double = 0,
                  icu_patients: Int = 0,
                  icu_patients_per_million: Double = 0,
                  hosp_patients: Int = 0,
                  hosp_patients_per_million: Double = 0,
                  weekly_hosp_admissions: Int = 0,
                  weekly_hosp_admissions_per_million: Double = 0,
                  total_tests: Int = 0,
                  new_tests: Int = 0,
                  total_tests_per_thousand: Double = 0,
                  new_tests_per_thousand: Double = 0,
                  new_tests_smoothed: Double = 0,
                  new_tests_smoothed_per_thousand: Double = 0,
                  positive_rate: Double = 0,
                  tests_per_case: Double = 0,
                  tests_units: String = null,
                  stringency_index: Double = 0
                )

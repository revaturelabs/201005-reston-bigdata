// Case classes for our big data project 3
import java.text.SimpleDateFormat
import java.util.Date

val dateFormat = new SimpleDateFormat("yyyy-MM-dd")

case class Region(
                   name: String = null,
                   agg_population: Long = 0,
                   avg_population_density: Double = 0,
                   avg_median_age: Double = 0,
                   avg_aged_65_older: Double = 0,
                   avg_aged_70_older: Double = 0,
                   avg_gdp_per_capita: Double = 0,
                   avg_cardiovasc_death_rate: Double = 0,
                   avg_diabetes_prevalence: Double = 0,
                   avg_handwashing_facilities: Double = 0,
                   avg_hospital_beds_per_thousand: Double = 0,
                   avg_life_expectancy: Double = 0,
                   avg_human_development_index: Double = 0,
                   agg_gdp: Long = 0,
                   agg_cases: Int = 0,
                   agg_recoveries: Int = 0,
                   agg_deaths: Int = 0,
                   countries: List[Country] = List(),
                   avg_unemployment: Double = 0
                 ) {}

case class Country(
                    name: String = null,
                    country_code: String = null,
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
                    unemployment: Double = 0,
                  ) {}

case class EconomicsData(
                          name: String = null,
                          gdp_constPrices: Long = 0,
                          gdp_constPrices_delta: Double = 0,
                          gdp_currentPrices: Long = 0,
                          gdp_currentPrices_usd: Long = 0,
                          gdp_currentPrices_ppp: Long = 0, //ppp = purchasing power parity
                          gdp_deflator: Int = 0,
                          gdp_perCap_constPrices: Long = 0,
                          gdp_perCap_constPrices_ppp: Double = 0,
                          gdp_perCap_currentPrices: Long = 0,
                          gdp_perCap_currentPrices_usd: Long = 0,
                          gdp_perCap_currentPrices_ppp: Double = 0,
                          output_gap_pGDP: Double = 0,
                          gdp_ppp_frac_of_total_world: Double = 0,
                          implied_ppp: Double = 0, //National Currency per current international dollar
                          total_investment: Double = 0, //as percent of GDP
                          gross_national_savings: Double = 0, //as percent of GDP
                          inflation_avgConsumerPrices: Double = 0,
                          inflation_avgConsumerPrices_delta: Double = 0,
                          inflation_eopConsumerPrices: Double = 0,
                          inflation_eopConsumerPrices_delta: Double = 0,
                          vol_imports_goods_and_services_delta: Double = 0,
                          vol_imports_goods_delta: Double = 0,
                          vol_exports_goods_and_services_delta: Double = 0,
                          vol_exports_goods_delta: Double = 0,
                          unemployment_rate: Double = 0,
                          employed_persons: Long = 0,
                          population: Long = 0,
                          government_revenue_currency: Long = 0,
                          government_revenue_percent: Double = 0,
                          government_total_expenditure_currency: Long = 0,
                          government_total_expenditure_percent: Double = 0,
                          government_net_lb_currency: Long = 0,
                          government_net_lb_percent: Double = 0,
                          government_structural_balance_currency: Long = 0,
                          government_structural_balance_percent_pGDP: Double = 0,
                          government_primary_net_lb_currency: Long = 0,
                          government_primary_net_lb_percent: Double = 0,
                          government_net_debt_currency: Long = 0,
                          government_net_debt_percent: Double = 0,
                          government_gross_debt_currency: Long = 0,
                          government_gross_debt_percent: Double = 0,
                          gdp_of_fiscal_year: Long = 0,
                          current_account_balance_usd: Long = 0,
                          current_account_balance_percentGDP: Double = 0
                        ) {}

case class cData(
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

case class Tweet(
                  timestamp: String = null,
                  id: Long = 0,
                  text: String = null,
                  // truncated: Boolean,
                  // coordinates: Option[GeoJSON],
                  // place: Option[TwitterPlace]
                ) {
  def getHashtags: List[String] = {
    val re = """(#\S+)""".r
    re.findAllIn( text ).toList
  }
}

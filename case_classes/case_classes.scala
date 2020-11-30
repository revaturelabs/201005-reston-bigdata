// Case classes for our big data project 3
import java.util.Date

case class Region (
    name: String,
    agg_population: Long,
    avg_population_density: Double,
    avg_median_age: Double,
    avg_aged_65_older: Double,
    avg_aged_70_older: Double,
    avg_gdp_per_capita: Double,
    avg_cardiovasc_death_rate: Double,
    avg_diabetes_prevalence: Double,
    avg_handwashing_facilities: Double,
    avg_hospital_beds_per_thousand: Double,
    avg_life_expectancy: Double,
    avg_human_development_index: Double,
    agg_gdp: Long,
    agg_cases: Int,
    agg_recoveries: Int,
    agg_deaths: Int,
    countries: List[Country],
    bordered_regions: List[Region],
    border_countries: List[Country],
    bordered_countries: List[Country],
    agg_case_data: List[cData],
    avg_unemployment: Double
) {}

case class Country (
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
    bordering_countries: List[Country] = List(),
    unemplyoment: Double = 0,
    case_data: List[cData] = List(),
    economics: EconomicsData = null
) {}

case class EconomicsData (
    gdp_constPrices: Long,
    gdp_constPrices_delta: Double,
    gdp_currentPrices: Long,
    gdp_currentPrices_usd: Long,
    gdp_currentPrices_ppp: Long,                           //ppp = purchasing power parity
    gdp_deflator: Int,
    gdp_perCap_constPrices: Long,
    gdp_perCap_constPrices_ppp: Double,
    gdp_perCap_currentPrices: Long,
    gdp_perCap_currentPrices_usd: Long,
    gdp_perCap_currentPrices_ppp: Double,
    output_gap_pGDP: Double,
    gdp_ppp_frac_of_total_world: Double,
    implied_ppp: Double,                                    //National Currency per current international dollar 
    total_investment: Double,                               //as percent of GDP
    gross_national_savings: Double,                         //as percent of GDP
    inflation_avgConsumerPrices: Double,
    inflation_avgConsumerPrices_delta: Double,
    inflation_eopConsumerPrices: Double,
    inflation_eopConsumerPrices_delta: Double,
    vol_imports_goods_and_services_delta: Double,
    vol_imports_goods_delta: Double,
    vol_exports_goods_and_services_delta: Double,
    vol_exports_goods_delta: Double, 
    unemployment_rate: Double,
    employmed_persons: Long,
    population: Long,
    government_revenue_currency: Long,
    government_revenue_percent: Double,
    government_total_expenditure_currency: Long,
    government_total_expenditure_percent: Double,
    government_net_lb_currency: Long,
    government_net_lb_percent: Double,
    government_structural_balance_currency: Long,
    government_structural_balance_percent_pGDP: Double,
    government_primary_net_lb_currency: Long,
    government_primary_net_lb_percent: Double,
    government_net_debt_currency: Long,
    government_net_debt_percent: Double,
    government_gross_debt_currency: Long,
    government_gross_debt_percent: Double,
    gdp_of_fiscal_year: Long,
    current_account_balance_usd: Long,
    current_account_balance_percentGDP: Double
) {}

case class cData(
    date: Date,
    total_cases: Int,
    new_cases: Int,
    new_cases_smoothed: Double,
    total_deaths: Int,
    new_deaths: Int,
    new_deaths_smoothed: Double,
    total_cases_per_million: Double,
    new_cases_per_million: Double,
    new_cases_smoothed_per_million: Double,
    total_deaths_per_million: Double,
    new_deaths_per_million: Double,
    new_deaths_smoothed_per_million: Double,
    total_tests: Int,
    new_tests: Int,
    total_tests_per_thousand: Double,
    new_tests_per_thousand: Double,
    new_tests_smoothed: Double,
    new_tests_smoothed_per_thousand: Double,
    tests_units: String
) {}

case class Tweet (
  timestamp: String,
  id: Long,
  text: String,
  // truncated: Boolean,
  // coordinates: Option[GeoJSON],
  // place: Option[TwitterPlace]
) {
  def getHashtags: List[String] = {
    val re = """(#\S+)""".r
    re.findAllIn(text).toList
  }
}
#!/usr/bin/bash
#   covid_data/ directory holds all data TSVs
#   covid_data/countries_general_stats.tsv holds the general stats that do not change daily
#       each country is stored in a row of this file
#   covid_data/daily_stats.tsv holds the stats that may change from day to day
#       each day is stored in a row of the file named after the country
#       the country that a given day record corresponds to is in the COUNTRY field
#   covid_data/{COUNTRY_NAME}.json holds the json representation of that countries data
#   the first row 

mkdir covid_data
ALL_JSON=`cat ./owid-covid-data.json`;
COUNTRIES=`echo $ALL_JSON | jshon -Q -k`
echo -e "CONTINENT\tCOUNTRY\tPOPULATION\tPOP_DENSITY\tMEDIAN_AGE\tOLDER65\tOLDER70\tEXTREME_POVERTY\tGDP_PER_CAPITA\tCARDIOVASCULAR_DEATH_RATE\tDIABETES_PREVALENCE\tFEMALE_SMOKERS\tMALE_SMOKERS\tHANDWASHING_FACILITIES\tHOSPITAL_BEDS_PER_THOUSAND\tLIFE_EXPECTANCY\tHUMAN_DEVELOPMENT_INDEX" > covid_data/countries_general_stats.tsv

echo -e "DATE\tCOUNTRY\tTOTAL_CASES\tNEW_CASES\tNEW_CASES_SMOOTHED\tTOTAL_DEATHS\tNEW_DEATHS\tNEW_DEATHS_SMOOTHED\tTOTAL_CASES_PER_MILLION\tNEW_CASES_PER_MILLION\tNEW_CASES_SMOOTHED_PER_MILLION\tTOTAL_DEATHS_PER_MILLION\tNEW_DEATHS_PER_MILLION\tNEW_DEATHS_PER_MILLION\tREPRODUCTION_RATE\tICU_PATIENTS\tICU_PATIENTS_PER_MILLION\tHOSP_PATIENTS\tHOSP_PATIENTS_PER_MILLION\tWEEKLY_ICU_ADMISSIONS\tWEEKLY_ICU_ADMISSIONS_PER_MILLION\tWEEKLY_HOSP_ADMISSIONS\tWEEKLY_HOSP_ADMISSIONS_PER_MILLION\tTOTAL_TESTS\tNEW_TESTS\tNEW_TESTS_SMOOTHED\tTOTAL_TESTS_PER_THOUSAND\tNEW_TESTS_SMOOTHED_PER_THOUSAND\tTESTS_PER_CASE\tPOSITIVE_RATE\tTESTS_UNITS\tSTRINGENCY_INDEX" > covid_data/daily_stats.tsv

for i in $COUNTRIES;
do 
    JSON=`echo $ALL_JSON | jshon -Q -e $i`
    TAGS=`echo $JSON | jshon -Q -d data`
    DATA=`echo $JSON | jshon -Q -e data`
    DATA_LEN=`echo $DATA | jshon -Q -l`
    #   general stats
    CONTINENT=`echo $TAGS | jshon -Q -e continent`
    COUNTRY=`echo $TAGS | jshon -Q -e location`
    POPULATION=`echo $TAGS | jshon -Q -e population`
    POP_DENSITY=`echo $TAGS | jshon -Q -e population_density`
    MEDIAN_AGE=`echo $TAGS | jshon -Q -e median_age`
    OLDER65=`echo $TAGS | jshon -Q -e aged_65_older`
    OLDER70=`echo $TAGS | jshon -Q -e aged_70_older`
    EXTREME_POVERTY=`echo $TAGS | jshon -Q -e extreme_poverty`
    GDP_PER_CAPITA=`echo $TAGS | jshon -Q -e gdp_per_capita`
    CARDIOVASCULAR_DEATH_RATE=`echo $TAGS | jshon -Q -e cardiovasc_death_rate`
    DIABETES_PREVALENCE=`echo $TAGS | jshon -Q -e diabetes_prevalence`
    FEMALE_SMOKERS=`echo $TAGS | jshon -Q -e female_smokers`
    MALE_SMOKERS=`echo $TAGS | jshon -Q -e male_smokers`
    HANDWASHING_FACILITIES=`echo $TAGS | jshon -Q -e handwashing_facilities`
    HOSPITAL_BEDS_PER_THOUSAND=`echo $TAGS | jshon -Q -e hospital_beds_per_thousand`
    LIFE_EXPECTANCY=`echo $TAGS | jshon -Q -e life_expectancy`
    HUMAN_DEVELOPMENT_INDEX=`echo $TAGS | jshon -Q -e human_development_index`
    echo -e "$CONTINENT\t$COUNTRY\t$POPULATION\t$POP_DENSITY\t$MEDIAN_AGE\t$OLDER65\t$OLDER70\t$EXTREME_POVERTY\t$GDP_PER_CAPITA\t$CARDIOVASCULAR_DEATH_RATE\t$DIABETES_PREVALENCE\t$FEMALE_SMOKERS\t$MALE_SMOKERS\t$HANDWASHING_FACILITIES\t$HOSPITAL_BEDS_PER_THOUSAND\t$LIFE_EXPECTANCY\t$HUMAN_DEVELOPMENT_INDEX" >> covid_data/countries_general_stats.tsv

    #   daily stats
    COUNTRY=${COUNTRY#\"}
    COUNTRY=${COUNTRY%\"}
    COUNTRY="${COUNTRY// /_}"
    echo $JSON > covid_data/${COUNTRY}.json
    for i in `seq 0 ${DATA_LEN-1}`;
    do
        DATA_I=`echo $DATA | jshon -Q -e $i`
        DATE=`echo $DATA_I | jshon -Q -e date`
        TOTAL_CASES=`echo $DATA_I | jshon -Q -e total_cases`
        NEW_CASES=`echo $DATA_I | jshon -Q -e new_cases`
        NEW_CASES_SMOOTHED=`echo $DATA_I | jshon -Q -e new_cases_smoothed`
        TOTAL_DEATHS=`echo $DATA_I | jshon -Q -e total_deaths`
        NEW_DEATHS=`echo $DATA_I | jshon -Q -e new_deaths`
        NEW_DEATHS_SMOOTHED=`echo $DATA_I | jshon -Q -e new_deaths_smoothed`
        TOTAL_CASES_PER_MILLION=`echo $DATA_I | jshon -Q -e total_cases_per_million`
        NEW_CASES_PER_MILLION=`echo $DATA_I | jshon -Q -e new_cases_per_million`
        NEW_CASES_SMOOTHED_PER_MILLION=`echo $DATA_I | jshon -Q -e new_cases_smoothed_per_million`
        TOTAL_DEATHS_PER_MILLION=`echo $DATA_I | jshon -Q -e total_deaths_per_million`
        NEW_DEATHS_PER_MILLION=`echo $DATA_I | jshon -Q -e new_deaths_per_million`
        NEW_DEATHS_SMOOTHED_PER_MILLION=`echo $DATA_I | jshon -Q -e new_deaths_smoothed_per_million`
        REPRODUCTION_RATE=`echo $DATA_I | jshon -Q -e reproduction_rate`
        ICU_PATIENTS=`echo $DATA_I | jshon -Q -e icu_patients`
        ICU_PATIENTS_PER_MILLION=`echo $DATA_I | jshon -Q -e icu_patients_per_million`
        HOSP_PATIENTS=`echo $DATA_I | jshon -Q -e hosp_patients`
        HOSP_PATIENTS_PER_MILLION=`echo $DATA_I | jshon -Q -e hosp_patients_per_million`
        WEEKLY_ICU_ADMISSIONS=`echo $DATA_I | jshon -Q -e weekly_icu_admissions`
        WEEKLY_ICU_ADMISSIONS_PER_MILLION=`echo $DATA_I | jshon -Q -e weekly_icu_admissions_per_million`
        WEEKLY_HOSP_ADMISSIONS=`echo $DATA_I | jshon -Q -e weekly_hosp_admissions`
        WEEKLY_HOSP_ADMISSIONS_PER_MILLION=`echo $DATA_I | jshon -Q -e weekly_hosp_admissions_per_million`
        TOTAL_TESTS=`echo $DATA_I | jshon -Q -e total_tests`
        NEW_TESTS=`echo $DATA_I | jshon -Q -e new_tests`
        NEW_TESTS_SMOOTHED=`echo $DATA_I | jshon -Q -e new_test_smoothed`
        TOTAL_TESTS_PER_THOUSAND=`echo $DATA_I | jshon -Q -e total_tests_per_thousand`
        NEW_TESTS_PER_THOUSAND=`echo $DATA_I | jshon -Q -e new_tests_per_thousand`
        NEW_TESTS_SMOOTHED_PER_THOUSAND=`echo $DATA_I | jshon -Q -e new_tests_smoothed_per_thousand`
        TESTS_PER_CASE=`echo $DATA_I | jshon -Q -e tests_per_case`
        POSITIVE_RATE=`echo $DATA_I | jshon -Q -e positive_rate`
        TESTS_UNITS=`echo $DATA_I | jshon -Q -e tests_units`
        STRINGENCY_INDEX=`echo $DATA_I | jshon -Q -e stringency_index`
        echo -e "$DATE\t$COUNTRY\t$TOTAL_CASES\t$NEW_CASES\t$NEW_CASES_SMOOTHED\t$TOTAL_DEATHS\t$NEW_DEATHS\t$NEW_DEATHS_SMOOTHED\t$TOTAL_CASES_PER_MILLION\t$NEW_CASES_PER_MILLION\t$NEW_CASES_SMOOTHED_PER_MILLION\t$TOTAL_DEATHS_PER_MILLION\t$NEW_DEATHS_PER_MILLION\t$NEW_DEATHS_PER_MILLION\t$REPRODUCTION_RATE\t$ICU_PATIENTS\t$ICU_PATIENTS_PER_MILLION\t$HOSP_PATIENTS\t$HOSP_PATIENTS_PER_MILLION\t$WEEKLY_ICU_ADMISSIONS\t$WEEKLY_ICU_ADMISSIONS_PER_MILLION\t$WEEKLY_HOSP_ADMISSIONS\t$WEEKLY_HOSP_ADMISSIONS_PER_MILLION\t$TOTAL_TESTS\t$NEW_TESTS\t$NEW_TESTS_SMOOTHED\t$TOTAL_TESTS_PER_THOUSAND\t$NEW_TESTS_SMOOTHED_PER_THOUSAND\t$TESTS_PER_CASE\t$POSITIVE_RATE\t$TESTS_UNITS\t$STRINGENCY_INDEX" >> covid_data/daily_stats.tsv
    done
    #   set empty columns to NULL
    vi -c "%s/\t\t/\tNULL\t/g | wq" covid_data/daily_stats.tsv
done
#   set empty columns to NULL
vi -c "%s/\t\t/\tNULL\t/g | wq" covid_data/countries_general_stats.tsv

#tar -czf covid_data.tgz covid_data/  

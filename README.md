# 201005-reston-bigdata: Project 3
monorepo for 201005-reston-bigdata batch project 3

## Datasets
- Covid 19 Case Reporting from the CDC
  - [COVID-19 Case Surveillance Public Use Data | Data | Centers for Disease Control and Prevention](https://data.cdc.gov/Case-Surveillance/COVID-19-Case-Surveillance-Public-Use-Data/vbim-akqf/data)
- Conglomeration of datasets from the UN, European CDPC, Johns Hopkins, World Bank and national government reports 
  - [Our World in Data - Coronavirus Source Data](https://ourworldindata.org/coronavirus-source-data)
- World Economic Outlook Database ( Bi - Annual )
  - [IMF - World Economic Outlook Database](https://www.imf.org/en/Publications/SPROLLS/world-economic-outlook-databases#sort=%40imfdate%20descending)
- A large-scale COVID-19 Twitter chatter dataset for open scientific research and international collaboration from Panacea Lab - Georgia State University
  - [Zenodo link](https://zenodo.org/record/3783737#.X85hTmhKhPY)
- API call formatting and updated statistics for live updating application
  - [Disease.sh](https://disease.sh/docs/) - provides formatting for proper API calls
  - [worldometers](https://www.worldometers.info/coronavirus/) - provides statistics updated every 10 minutes


## Problem Statement
- Which Regions handled COVID-19 the best assuming our metrics are change in GDP by percentage and COVID-19 infection rate per capita. (Jan 1 2020 - Oct
31 2020)
- Find the top 5 pairs of countries that share a land border and have the highest discrepancy in covid-19 infection rate per capita. Additionally find the top 5
landlocked countries that have the highest discrepancy in covid-19 infection rate per capita.
- Live update by Region of current relevant totals from COVID-19 data.
- Is the trend of the global COVID-19 discussion going up or down? Do spikes in infection rates of the 5-30 age range affect the volume of discussion?
- When was COVID-19 being discussed the most?
- What percentage of countries have an increasing COVID-19 Infection rate?
- What are the hashtags used to describe COVID-19 by Region (e.g. #covid, #COVID-19, #Coronavirus, #NovelCoronavirus)? Additionally what are the top 10
commonly used hashtags used alongside COVID hashtags?
- Is there a significant relationship between a Region’s cumulative GDP and Infection Rate per capita? What is the average amount of time it took for each region
to reach its first peak in infection rate per capita?
- Project monitoring through ELK Stack


## Relevant Totals
- Infection Numbers
- Deaths
- Recoveries

## Regions
- Africa
- Asia
- The Caribbean
- Central America
- Europe
- North America
- South America
- Oceania

## Helm Charts Used
- Kafka: [bitnami/kafka](https://artifacthub.io/packages/helm/bitnami/kafka) uses Kafka 2.6.0
- Zookeeper: [bitnami/zookeeper](https://artifacthub.io/packages/helm/bitnami/zookeeper) uses Zookeeper 3.6.2
- Spark: [bitnami/spark](https://artifacthub.io/packages/helm/bitnami/spark) uses Spark 3.0.1
- Ingress: nginx
- Elastic Stack: Custom

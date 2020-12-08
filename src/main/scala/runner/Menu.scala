package runner

object Menu {

  def printMenu(): Unit ={
    // Add your input format in the help menu below:
    println("___________________________________Menu___________________________________")
    println("Q1   | Regions ranked by average GDP and infection rates")
    println("Q2   | Discrepancies between bordering and land-locked countries")
    println("Q3   | COVID live update by region")
    println("Q4   | COVID-19 discussion trend")
    println("Q5   | COVID-19 peak discussion")
    println("Q6   | Percentage of countries with increasing COVID-19 Infection rate")
    println("Q7   | Hashtags used to describe COVID-19")
    println("Q8   | Regional correlation between infection rate and GDP")
    println("Exit | Leave the program")
    println("__________________________________________________________________________")
  }

}

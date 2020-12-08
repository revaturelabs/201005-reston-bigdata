package main.scala.blue

import java.text.SimpleDateFormat

object DateFunc {
  def dayInYear(date: String, firstofyear: Long = 1577865600000L): Int = {
    //    val firstofyear = new GregorianCalendar(year, Calendar.JANUARY, 1).getTime.getTime
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    ((dateFormat.parse(date).getTime - firstofyear) / 86400000).toInt
  }
}

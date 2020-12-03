package blue

import java.util.Date

object DateFunc {
  def dayInYear(date: Date, firstofyear: Long = 1577865600000L): Int ={
//    val firstofyear = new GregorianCalendar(year, Calendar.JANUARY, 1).getTime.getTime
    ((date.getTime - firstofyear)/86400000).toInt
  }
}

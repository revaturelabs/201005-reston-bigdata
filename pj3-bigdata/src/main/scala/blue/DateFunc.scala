package blue

import java.text.SimpleDateFormat
import java.util.Date

object DateFunc {
  def dayInYear(date: String, firstofyear: Long = 1577865600000L): Int ={
    //    val firstofyear = new GregorianCalendar(year, Calendar.JANUARY, 1).getTime.getTime
    val dateFormat = new SimpleDateFormat("yyyy-MM-dd")
    ((dateFormat.parse(date).getTime - firstofyear)/86400000).toInt
  }
}

package molecule.util
import java.text.SimpleDateFormat
import java.time.{LocalDate, ZoneId}
import java.util.{Date, TimeZone}

trait Helpers {

  object date {
    def apply(year: Int, month: Int, day: Int): Date = {
      val localDate = LocalDate.of(year, month, day)
      val instant = localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant
      Date.from(instant)
    }
  }

  // Todo: need advice on handling time zones!

  def format(date: Date): String = {
    val f = new SimpleDateFormat("'#inst \"'yyyy-MM-dd'T'HH:mm:ss.SSSXXX'\"'")
//    f.setTimeZone(TimeZone.getTimeZone("UTC"))
    f.format(date)
  }

  def format2(date: Date): String = {
    val f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
//    f.setTimeZone(TimeZone.getTimeZone("UTC"))
    f.format(date)
  }

  def f(a: Any) = a match {
    case date: Date => format2(date).replace("+", "\\\\+")
    case other      => other
  }
  def f2(a: Any) = a match {
    case date: Date => format2(date)
    case other      => other
  }
}
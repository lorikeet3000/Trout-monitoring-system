package ru.arvata.pomor.util

import android.text.format.DateUtils
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

typealias HHmm_ddMMyyyy = String
typealias HHmm = String
typealias ddMMyyyy = String
typealias isoDateTimeString = String // "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
typealias relativeDateTimeString = String // ""

fun isDatesEqual(first: ddMMyyyy, second: isoDateTimeString): Boolean {
    return try {
        val secondDate = isoDateTimeStringFormat.parse(second)
        val secondFormat = ddMMyyyyFormat.format(secondDate)
        first == secondFormat
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }
}

fun isToday(date: isoDateTimeString): Boolean {
    val first = getDdMMyyyyString(date)
    val today = getToday()
    loge("$first $today ${first == today}")
    return first == today
}

fun isYesterday(date: isoDateTimeString): Boolean {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_MONTH, -1)
    val yesterday = getDdMMyyyyString(cal.time)
    val first = getDdMMyyyyString(date)
    loge("$first $yesterday ${first == yesterday}")
    return first == yesterday
}

fun getDdMMyyyyString(date: Date): ddMMyyyy = ddMMyyyyFormat.format(date)

fun getDdMMyyyyString(date: isoDateTimeString): ddMMyyyy {
    return try {
        val parsed = isoDateTimeStringFormat.parse(date)
        ddMMyyyyFormat.format(parsed)
    } catch (e: Exception) {
        e.printStackTrace()
        date
    }
}

fun getDdMMyyyyString(day: Int, month: Int): ddMMyyyy? {
    return try {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, day)
        cal.set(Calendar.MONTH, month)
        ddMMyyyyFormat.format(cal.time)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun getHhmm_ddMMyyyyString(date: isoDateTimeString): HHmm_ddMMyyyy {
    return try {
        val parsed = isoDateTimeStringFormat.parse(date)
        HHmm_ddMMyyyyFormat.format(parsed)
    } catch (e: Exception) {
        loge(e.message)
//        e.printStackTrace()
        date
    }
}

fun getHhmmString(date: isoDateTimeString): HHmm {
    return try {
        val parsed = isoDateTimeStringFormat.parse(date)
        HHmmFormat.format(parsed)
    } catch (e: Exception) {
        e.printStackTrace()
        date
    }
}

fun getRelativeDateTimeString(date: isoDateTimeString): relativeDateTimeString {
    return try {
        val parsed = isoDateTimeStringFormat.parse(date)
        DateUtils.getRelativeDateTimeString(App.appContext, parsed.time,
            DateUtils.MINUTE_IN_MILLIS, DateUtils.DAY_IN_MILLIS * 2, 0).toString()
    } catch (e: Exception) {
        loge(e.message ?: "")
        date
    }
}

fun getShortDateTimeString(date: isoDateTimeString): String {
    return try {
        val parsed = isoDateTimeStringFormat.parse(date)
        HHmm_ddMMFormat.format(parsed)
    } catch (e: Exception) {
        e.printStackTrace()
        date
    }
}

fun isOverdue(date: isoDateTimeString, deadline: isoDateTimeString): Boolean {
//    loge("date: $date, deadline: $deadline")
    val first = isoDateTimeStringFormat.parse(date)
    val second = isoDateTimeStringFormat.parse(deadline)
    return first.after(second)
}

fun isDateInPeriod(date: ddMMyyyy, start: isoDateTimeString, end: isoDateTimeString): Boolean {
    try {
        val yyyyMMddFormat = SimpleDateFormat("yyyy.MM.dd", Locale("ru-RU"))

        val d = yyyyMMddFormat.format(ddMMyyyyFormat.parse(date))
        val s = yyyyMMddFormat.format(isoDateTimeStringFormat.parse(start))
        val e =  yyyyMMddFormat.format(isoDateTimeStringFormat.parse(end))

        return d in s..e
    } catch (e: Exception) {
        return false
    }
}

fun isDateInPeriod(hours: Int, dateString: isoDateTimeString): Boolean {
    try {
        val dateParsed = isoDateTimeStringFormat.parse(dateString)
        val cal = Calendar.getInstance()
        cal.time = dateParsed
        val dateYear = cal.get(Calendar.YEAR)
        val dateMonth = cal.get(Calendar.MONTH)
        val dateDay = cal.get(Calendar.DAY_OF_MONTH)
        val dateHour = cal.get(Calendar.HOUR_OF_DAY)

        val periodEnd = Calendar.getInstance()
        periodEnd.add(Calendar.HOUR_OF_DAY, -hours)
        val endYear = periodEnd.get(Calendar.YEAR)
        val endMonth = periodEnd.get(Calendar.MONTH)
        val endDay = periodEnd.get(Calendar.DAY_OF_MONTH)
        val endHour = periodEnd.get(Calendar.HOUR_OF_DAY)

//    loge("period end: $endYear.$endMonth.$endDay:$endHour")
//    loge("date: $dateYear.$dateMonth.$dateDay:$dateHour")

        // true if date > periodEnd

        val result = dateYear > endYear ||
                (dateYear == endYear && dateMonth > endMonth) ||
                (dateYear == endYear && dateMonth == endMonth && dateDay > endDay) ||
                (dateYear == endYear && dateMonth == endMonth && dateDay == endDay && dateHour > endHour)

        return result
    } catch (e: Exception) {
        e.printStackTrace()
        return false
    }
}

fun getTimePointBetween(firstPoint: isoDateTimeString, lastPoint: isoDateTimeString, offset: Float): isoDateTimeString {
    // firstPoint + (lastPoint - firstPoint) * offset
    // offset should be 1/3, 2/3, etc.
    return try {
        val firstPointMs = isoDateTimeStringFormat.parse(firstPoint).time
        val lastPointMs = isoDateTimeStringFormat.parse(lastPoint).time
        val pointMs = firstPointMs + ((lastPointMs - firstPointMs).toFloat() * offset).toLong()
        isoDateTimeStringFormat.format(Date(pointMs))
    } catch (e: Exception) {
        firstPoint
    }
}

fun getIsoDateTimeString(year: Int, month: Int, day: Int, hour: Int, minute: Int): isoDateTimeString? {
    return try {
        val cal = Calendar.getInstance()
        cal.set(year, month, day, hour, minute)
        isoDateTimeStringFormat.format(cal.time)
    } catch (e: Exception) {
        null
    }
}

fun getIsoDateTimeString(date: Date): isoDateTimeString {
    return isoDateTimeStringFormat.format(date)
}

fun getIsoDateTimeString(date: ddMMyyyy): isoDateTimeString {
    return try {
        val parsed = ddMMyyyyFormat.parse(date)
        isoDateTimeStringFormat.format(parsed)
    } catch (e: Exception) {
        e.printStackTrace()
        date
    }
}

// What's the time?
// It's now
// ??????????
fun getCurrentTime(): isoDateTimeString {
    return getIsoDateTimeString(Calendar.getInstance().time)
}

fun getToday(): ddMMyyyy {
    val time = Calendar.getInstance().time
    return ddMMyyyyFormat.format(time)
}

fun getTomorrow(): ddMMyyyy {
    val cal = Calendar.getInstance()
    cal.add(Calendar.DAY_OF_MONTH, 1)
    return ddMMyyyyFormat.format(cal.time)
}

fun getDdMMMString(date: ddMMyyyy): String {
    return try {
        val parsed = ddMMyyyyFormat.parse(date)
        ddMMMFormat.format(parsed)
    } catch (e: Exception) {
        e.printStackTrace()
        date
    }
}

private val ddMMyyyyFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru-RU"))
private val ddMMMFormat = SimpleDateFormat("dd MMMM", Locale.getDefault())
private val HHmm_ddMMyyyyFormat = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale("ru-RU"))
private val HHmm_ddMMFormat = SimpleDateFormat("dd.MM, HH:mm", Locale("ru-RU"))
private val isoDateTimeStringFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale("ru-RU"))
private val HHmmFormat = SimpleDateFormat("HH:mm", Locale("ru-RU"))
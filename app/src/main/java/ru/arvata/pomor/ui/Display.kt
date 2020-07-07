package ru.arvata.pomor.ui

import android.support.v4.content.ContextCompat
import android.widget.TextView
import ru.arvata.pomor.R
import ru.arvata.pomor.data.*
import ru.arvata.pomor.data.database.TroutTypeConverters

private fun getFloatDisplay(value: Float?): String = if(value != null) { "%.1f".format(value) } else { "?" }

fun getSiteDisplay(site: Site?): String = site?.name ?: "Неизвестный участок"
fun getTankNumberDisplay(tank: Tank?): String = if(tank != null) { "Садок №${tank.number}" } else { "Неизвестный садок" }
fun getTankWithSiteDisplay(tank: Tank?, site: Site?): String = "${getTankNumberDisplay(tank)}, ${getSiteDisplay(site)}"
fun getUserNameDisplay(user: User?): String = user?.name ?: "Неизвестный пользователь"
fun getFishWeightDisplay(amount: Float?): String = if(amount != null) { "${"%.3f".format(amount)} кг/шт" } else { "?" }
fun getAllWeightDisplay(amount: Float?): String = if(amount != null) { "${"%.1f".format(amount)} кг" } else { "?" }
fun getFishAmountDisplay(amount: Int?): String = if(amount != null) { "$amount шт" } else { "? шт" }

fun getTemperatureIndicatorDisplay(indicator: Float?): String = "${getFloatDisplay(indicator)}℃"
fun getOxygenIndicatorDisplay(indicator: Float?): String = getFloatDisplay(indicator)
fun getTemperatureIndicatorDisplay(tank: Tank?): String = getTemperatureIndicatorDisplay(tank?.temperature?.indicator?.toFloat())
fun getOxygenIndicatorDisplay(tank: Tank?): String = getOxygenIndicatorDisplay(tank?.oxygen?.indicator?.toFloat())

fun getFeedingIndicatorDisplay(tank: Tank?): String = getFloatDisplay(tank?.feeding?.indicator?.toFloat()) + " г"
fun getFeedProducerDisplay(producer: FeedProducer?): String = producer?.name ?: "Неизвестный производитель"

fun getMortalityIndicatorDisplay(tank: Tank?): String = getFishAmountDisplay(tank?.mortality?.indicator?.toInt())
fun getSeedingIndicatorDisplay(tank: Tank?): String = getFishAmountDisplay(tank?.seeding?.indicator?.toInt())
fun getMovingIndicatorDisplay(tank: Tank?): String = getFishAmountDisplay(tank?.moving?.indicator?.toInt())
fun getCatchIndicatorDisplay(tank: Tank?): String = getFishAmountDisplay(tank?.catch?.indicator?.toInt())

fun getEventDescriptionDisplay(description: String): String {
    return when (TroutTypeConverters.stringToIndicatorTypeNullable(description)) {
        IndicatorType.Weight -> "Добавлен показатель навески"
        IndicatorType.Temperature -> "Добавлен показатель температуры"
        IndicatorType.Oxygen -> "Добавлен показатель кислорода"
        IndicatorType.Feeding -> "Добавлен показатель кормления"
        IndicatorType.Seeding -> "Добавлен показатель зарыбления"
        IndicatorType.Mortality -> "Добавлен показатель отхода"
        IndicatorType.Moving -> "Добавлен показатель перемещения"
        IndicatorType.Catch -> "Добавлен показатель вылова"
        else -> description
    }
}

fun getDiffDisplay(diff: Float): String {
    val diffDisplay = getFloatDisplay(diff)
    return if(diff > 0) {
        "(+$diffDisplay)"
    } else {
        "($diffDisplay)"
    }
}

const val UNKNOWN_TIME = "Неизвестное время"

enum class TankColor {
    Red, Yellow, Green, Unknown
}

fun getTankColor(tank: Tank?): TankColor {
    val temperatureColor = getTemperatureColor(tank)
    val oxygenColor = getOxygenColor(tank)

    if(temperatureColor == TankColor.Red || oxygenColor == TankColor.Red) {
        return TankColor.Red
    }

    if(temperatureColor == TankColor.Yellow || oxygenColor == TankColor.Yellow) {
        return TankColor.Yellow
    }

    return TankColor.Green
}

private val temperatureGreenZone = 0.0f..19.0f
private val temperatureYellowZone = 19.1f.. 20.9f
private val temperatureRedZone = 21.0f..100.0f

typealias ColorFunction = (Float?) -> TankColor
typealias IndicatorDisplayFunction = (Float?) -> String

fun getTemperatureColor(tank: Tank?): TankColor = getTemperatureColor(tank?.temperature?.indicator as? Float)

fun getTemperatureColor(indicator: Float?): TankColor {
    return when (indicator) {
        null -> TankColor.Unknown
        in temperatureGreenZone -> TankColor.Green
        in temperatureYellowZone -> TankColor.Yellow
        else -> TankColor.Red
    }
}

fun getOxygenColor(tank: Tank?): TankColor = getOxygenColor(tank?.oxygen?.indicator as? Float)

fun getOxygenColor(indicator: Float?): TankColor {
    return when (indicator) {
        null -> TankColor.Unknown
        in 0.0f..7.0f -> TankColor.Red
        in 7.1f..7.9f -> TankColor.Yellow
        else -> TankColor.Green
    }
}

fun getFeedingColor(): TankColor = TankColor.Unknown
fun getMortalityColor(): TankColor = TankColor.Unknown
fun getSeedingColor(): TankColor = TankColor.Unknown
fun getMovingColor(): TankColor = TankColor.Unknown
fun getCatchColor(): TankColor = TankColor.Unknown

fun TextView.setTankColor(color: TankColor) {
    val colorId = when (color) {
        TankColor.Red -> R.color.colorRed
        TankColor.Yellow -> R.color.colorYellow
        TankColor.Green -> R.color.colorGreen
        TankColor.Unknown -> R.color.colorGrey
    }

    val textColorId = when (color) {
        TankColor.Red -> R.color.textColorOnRed
        TankColor.Yellow -> R.color.textColorOnYellow
        TankColor.Green -> R.color.textColorOnGreen
        TankColor.Unknown -> R.color.textColorOnGrey
    }

    backgroundTintList = ContextCompat.getColorStateList(this.context, colorId)
    setTextColor(ContextCompat.getColor(this.context, textColorId))
}

fun TextView.setIndicatorColor(color: TankColor) {
    val colorId = when (color) {
        TankColor.Red -> R.color.colorRedLight
        TankColor.Yellow -> R.color.colorYellowLight
        TankColor.Green -> R.color.colorGreenLight
        TankColor.Unknown -> R.color.colorGrey
    }

    val textColorId = when (color) {
        TankColor.Red -> R.color.textColorOnRedLight
        TankColor.Yellow -> R.color.textColorOnYellowLight
        TankColor.Green -> R.color.textColorOnGreenLight
        TankColor.Unknown -> R.color.textColorOnGrey
    }

    backgroundTintList = ContextCompat.getColorStateList(this.context, colorId)
    setTextColor(ContextCompat.getColor(this.context, textColorId))
}

fun TextView.setPlanStatus(status: PlanStatus) {
    val colorId: Int

    if(status == PlanStatus.Completed) {
        text = "Выполнено"
        colorId = R.color.colorGreenLight
    } else if(status == PlanStatus.Canceled) {
        text = "Отменено"
        colorId = R.color.colorRedLight
    } else {
        text = "Не выполнено"
        colorId = R.color.colorYellowLight
    }

    backgroundTintList = ContextCompat.getColorStateList(this.context, colorId)
}

fun TextView.setOverdue(overdue: Boolean) {
    if(overdue) {
        background = ContextCompat.getDrawable(this.context, R.drawable.rounded_background)
        backgroundTintList = ContextCompat.getColorStateList(this.context, R.color.colorRedLight)
    } else {
        background = null
    }
}

fun PlanStatus.toDisplayString(): String {
    return when(this) {
        PlanStatus.Completed -> "Выполненные"
        PlanStatus.NotCompleted -> "Не выполненные"
        PlanStatus.Canceled -> "Отказ"
    }
}
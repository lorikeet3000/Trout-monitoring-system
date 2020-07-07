package ru.arvata.pomor.data

import ru.arvata.pomor.data.database.TroutTypeConverters
import ru.arvata.pomor.util.ddMMyyyy
import ru.arvata.pomor.util.getCurrentTime
import ru.arvata.pomor.util.isoDateTimeString

data class User(val id: String,
                val name: String,
                val login: String,
                val role: String)

data class FeedProducer(val id: String, val name: String)

data class Site(val id: String, val name: String)

data class Tank(val id: String,
                val siteId: String,
                val number: Int,
                val fishAmount: Int,
                val fishWeight: TankIndicator?,
                val temperature: TankIndicator?,
                val oxygen: TankIndicator?,
                val feeding: TankIndicator?,
                val mortality: TankIndicator?,
                val seeding: TankIndicator?,
                val moving: TankIndicator?,
                val catch: TankIndicator?) {

    fun allWeight(): Float = (fishWeight?.indicator as? Float ?: 0.0f) * (fishAmount.toFloat())
}

// индикатор, для которого уже известен Tank и тип индикатора, используется только в классе TankIndicators
data class TankIndicator(val indicator: Number, val time: isoDateTimeString,
                         val feedProducer: FeedProducer? = null,
                         val movingTankId: String? = null, val movingReason: String? = null,
                         val catchReason: String? = null)

data class Indicator(val id: String, val indicator: Number, val time: isoDateTimeString, val tank: Tank, val type: IndicatorType,
                     val feedProducer: FeedProducer? = null, val feedCoef: Float? = null,
                     val feedPeriodFrom: ddMMyyyy? = null, val feedPeriodTo: ddMMyyyy? = null,
                     val movingTank: Tank? = null, val movingReason: String? = null, val catchReason: String? = null)

fun getIndicatorId(timestamp: isoDateTimeString, type: IndicatorType): String =
    "${timestamp}_${TroutTypeConverters.indicatorTypeToString(type)}"

enum class IndicatorType {
    Weight, Temperature, Oxygen, Feeding, Seeding, Mortality, Moving, Catch
}

data class Event(val id: String, val site: Site?, val tank: Tank?, val user: User?, val time: isoDateTimeString, val description: String)

data class Plan(val id: String, val title: String, val description: String?, val createdAt: isoDateTimeString,
                val createdBy: User?, val dueFrom: isoDateTimeString, val dueTo: isoDateTimeString,
                val executors: List<User>, val tanks: List<Pair<Tank, Site?>>, val repeat: String, val status: PlanStatus,
                val completedAt: isoDateTimeString?, val completedBy: User?, val comment: String?) {

    fun isOverdue(user: User?): Boolean {
        return status == PlanStatus.NotCompleted
                && executors.contains(user)
                && ru.arvata.pomor.util.isOverdue(getCurrentTime(), dueTo)
    }

    fun canEdit(user: User?): Boolean {
        return createdBy != null && user != null && createdBy.id == user.id
    }

    fun canComplete(user: User?): Boolean {
        return user != null && executors.find {
            user.id == it.id
        } != null
    }
}

enum class PlanStatus {
    Completed, NotCompleted, Canceled
}

const val PLAN_REPEAT_NO_REPEAT = "no repeat"

sealed class IndicatorsConstants {
    abstract val minimumValue: Float
    abstract val maximumValue: Float
    abstract val topLimit: Float
    abstract val bottomLimit: Float
}

object TemperatureConstants : IndicatorsConstants() {
    override val minimumValue: Float = 16.0f
    override val maximumValue: Float = 24.0f
    override val topLimit: Float = 21.0f
    override val bottomLimit: Float = 19.0f
}

object OxygenConstants : IndicatorsConstants() {
    override val minimumValue: Float = 5.0f
    override val maximumValue: Float = 10.0f
    override val topLimit: Float = 8.0f
    override val bottomLimit: Float = 7.0f
}
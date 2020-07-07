package ru.arvata.pomor.data.network

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import ru.arvata.pomor.data.database.IndicatorEntity
import ru.arvata.pomor.util.isoDateTimeString

data class CredentialsNetwork(val login: String,
                              val password: String)

data class UserNetwork(val id: String,
                       val email: String,
                       val name: String,
                       val login: String,
                       val role: String)

data class FeedProducerNetwork(val id: String,
                               val name: String)

data class SiteNetwork(val id: String,
                       val name: String)

data class IndicatorNetwork(val id: String,
                            val indicator: Float,
                            val timestamp: isoDateTimeString,
                            val tankId: String,
                            val type: String,
                            val feedProducerId: String?,
                            @SerializedName("feedRatio")
                            val feedCoef: Float?,
                            @SerializedName("feedRangeFrom")
                            val feedPeriodFrom: isoDateTimeString?,
                            @SerializedName("feedRangeTo")
                            val feedPeriodTo: isoDateTimeString?,
                            val movingTankId: String?,
                            val movingReason: String?,
                            val catchReason: String?) {
    fun toEntity(uploaded: Boolean, userId: String?, siteId: String?): IndicatorEntity {
        return IndicatorEntity(id, indicator, timestamp, tankId, type, uploaded, userId, siteId,
            feedProducerId, feedCoef, feedPeriodFrom, feedPeriodTo,
            movingTankId, movingReason, catchReason)
    }
}

data class TankNetwork(val id: String,
                       val number: Int,
                       val siteId: String,
                       val fishAmount: Int,
                       val indicators: TankIndicatorsNetwork)

data class TankIndicatorsNetwork(val fishWeight: IndicatorNetwork?,
                                 val temperature: IndicatorNetwork?,
                                 val oxygen: IndicatorNetwork?,
                                 val feeding: IndicatorNetwork?,
                                 val seeding: IndicatorNetwork?,
                                 val mortality: IndicatorNetwork?,
                                 val moving: IndicatorNetwork?,
                                 val catch: IndicatorNetwork?)

data class EventNetwork(val id: String,
                        val userId: String,
                        val siteId: String,
                        val tankId: String,
                        val description: String,
                        val timestamp: isoDateTimeString)

data class SummaryNetwork(
    val users: List<UserNetwork>,
    val feedProducers: List<FeedProducerNetwork>,
    val sites: List<SiteNetwork>,
    val tanks: List<TankNetwork>,
    val temperatureIndicators: List<IndicatorNetwork>,
    val oxygenIndicators: List<IndicatorNetwork>,
    val events: List<EventNetwork>,
    @SerializedName("tasks")
    val plans: List<PlanNetwork>
)

data class PlanNetwork(
    val id: String,
    val title: String,
    val description: String?,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("createdBy")
    val createdBy: String,
    val dueFrom: String,
    val dueTo: String,
    val executorIds: List<String>,
    val tankIds: List<String>,
    val status: String,
    val completedAt: String?,
    val completedBy: String?,
    val comment: String?
)

data class CreatePlanNetwork(
    @SerializedName("createdBy")
    val createdBy: String,
    val dueFrom: String,
    val dueTo: String,
    val executorIds: List<String>,
    val tankIds: List<String>,
    val title: String,
    val description: String?
)

data class PlanStatusNetwork(
    val comment: String?,
    val completedBy: String?,
    val status: String
)
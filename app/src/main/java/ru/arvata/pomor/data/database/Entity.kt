package ru.arvata.pomor.data.database

import android.arch.persistence.room.*
import ru.arvata.pomor.util.isoDateTimeString

@Entity(tableName = "tblUser")
class UserEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val login: String,
    val role: String
)

@Entity(tableName = "tblFeedProducer")
class FeedProducerEntity(
    @PrimaryKey
    val id: String,
    val name: String
)

@Entity(tableName = "tblSite")
class SiteEntity(
    @PrimaryKey
    val id: String,
    val name: String
)

@Entity(tableName = "tblTank")
data class TankEntity(
    @PrimaryKey
    val id: String,
    val siteId: String,
    val number: Int,
    val fishAmount: Int
)

@Entity(tableName = "tblEvent")
data class EventEntity(
    @PrimaryKey
    val id: String,
    val userId: String?,
    val tankId: String,
    val timestamp: String,
    val description: String,
    val siteId: String?
)

@Entity(tableName = "tblIndicator")
data class IndicatorEntity(
    @PrimaryKey
    val id: String,
    val indicator: Float,
    val timestamp: isoDateTimeString,
    val tankId: String,
    val type: String,
    val uploaded: Boolean,
    val userId: String?,
    val siteId: String?,
    val feedProducerId: String?,
    val feedCoef: Float?,
    val feedPeriodFrom: isoDateTimeString?,
    val feedPeriodTo: isoDateTimeString?,
    val movingTankId: String?,
    val movingReason: String?,
    val catchReason: String?
)
//
//class TankWithIndicatorsEntity(@Embedded val tankEntity: TankEntity) {
//    @Relation(parentColumn = "id", entityColumn = "tankId")
//    var indicators: List<IndicatorEntity> = listOf()
//}

@Entity(tableName = "tblPlan")
data class PlanEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val description: String?,
    val createdAt: String,
    val createdBy: String,
    val dueFrom: String,
    val dueTo: String,
    val executorIds: List<String>,
    val tankIds: List<String>,
    val repeat: String,
    val status: String,
    val completedAt: String?,
    val completedBy: String?,
    val comment: String?
)
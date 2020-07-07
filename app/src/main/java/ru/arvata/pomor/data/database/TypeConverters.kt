package ru.arvata.pomor.data.database

import android.arch.persistence.room.TypeConverter
import ru.arvata.pomor.data.IndicatorType
import ru.arvata.pomor.data.PlanStatus

object TroutTypeConverters {
    const val INDICATOR_WEIGHT = "fishWeight"
    const val INDICATOR_TEMPERATURE = "temperature"
    const val INDICATOR_OXYGEN = "oxygen"
    const val INDICATOR_FEEDING = "feeding"
    const val INDICATOR_SEEDING = "seeding"
    const val INDICATOR_MORTALITY = "mortality"
    const val INDICATOR_MOVING = "moving"
    const val INDICATOR_CATCH = "catch"

    const val CATCH_REASON_VET = "vetExamination"
    const val CATCH_REASON_OTHER = "other"

    const val PLAN_STATUS_COMPLETED = "completed"
    const val PLAN_STATUS_NOT_COMPLETED = "not completed"
    const val PLAN_STATUS_CANCELED = "canceled"

    @TypeConverter
    @JvmStatic
    fun indicatorTypeToString(value: IndicatorType): String =  when(value) {
        IndicatorType.Weight -> INDICATOR_WEIGHT
        IndicatorType.Temperature -> INDICATOR_TEMPERATURE
        IndicatorType.Oxygen -> INDICATOR_OXYGEN
        IndicatorType.Feeding -> INDICATOR_FEEDING
        IndicatorType.Seeding -> INDICATOR_SEEDING
        IndicatorType.Mortality -> INDICATOR_MORTALITY
        IndicatorType.Moving -> INDICATOR_MOVING
        IndicatorType.Catch -> INDICATOR_CATCH
    }

    @TypeConverter
    @JvmStatic
    fun stringToIndicatorType(value: String): IndicatorType = when(value) {
        INDICATOR_WEIGHT -> IndicatorType.Weight
        INDICATOR_TEMPERATURE -> IndicatorType.Temperature
        INDICATOR_OXYGEN -> IndicatorType.Oxygen
        INDICATOR_FEEDING -> IndicatorType.Feeding
        INDICATOR_SEEDING -> IndicatorType.Seeding
        INDICATOR_MORTALITY -> IndicatorType.Mortality
        INDICATOR_MOVING -> IndicatorType.Moving
        INDICATOR_CATCH -> IndicatorType.Catch
        else -> IndicatorType.Weight
    }

    fun stringToIndicatorTypeNullable(value: String): IndicatorType? = when(value) {
        INDICATOR_WEIGHT -> IndicatorType.Weight
        INDICATOR_TEMPERATURE -> IndicatorType.Temperature
        INDICATOR_OXYGEN -> IndicatorType.Oxygen
        INDICATOR_FEEDING -> IndicatorType.Feeding
        INDICATOR_SEEDING -> IndicatorType.Seeding
        INDICATOR_MORTALITY -> IndicatorType.Mortality
        INDICATOR_MOVING -> IndicatorType.Moving
        INDICATOR_CATCH -> IndicatorType.Catch
        else -> null
    }

    private const val SEPARATOR = ";"

    @TypeConverter
    @JvmStatic
    fun listOfStringsToString(value: List<String>): String {
        return value.joinToString(SEPARATOR)
    }

    @TypeConverter
    @JvmStatic
    fun stringToListOfStrings(value: String): List<String> {
        return value.split(SEPARATOR)
    }

    fun stringToPlanStatus(value: String): PlanStatus {
        return when(value) {
            PLAN_STATUS_COMPLETED -> PlanStatus.Completed
            PLAN_STATUS_CANCELED -> PlanStatus.Canceled
            else -> PlanStatus.NotCompleted
        }
    }

    fun planStatusToString(value: PlanStatus): String {
        return when(value) {
            PlanStatus.Completed -> PLAN_STATUS_COMPLETED
            PlanStatus.Canceled -> PLAN_STATUS_CANCELED
            PlanStatus.NotCompleted -> PLAN_STATUS_NOT_COMPLETED
        }
    }
}
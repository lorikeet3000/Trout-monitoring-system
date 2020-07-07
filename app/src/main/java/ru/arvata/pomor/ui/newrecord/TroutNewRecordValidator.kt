package ru.arvata.pomor.ui.newrecord

import ru.arvata.pomor.data.IndicatorType
import ru.arvata.pomor.data.Site
import ru.arvata.pomor.data.Tank
import ru.arvata.pomor.util.loge

interface NewRecordValidator {
    fun validateRecord(): ValidationResult

    fun validateTankNumber(tankNumber: Int): Boolean

    fun validateDestinationTankNumber(tankNumber: Int): Boolean

    fun setSites(sites: List<Site>?)

    fun setAllTanks(tanks: List<Tank>?)

    fun setRecord(record: Record?)

    fun getSites(): List<Site>?
}

enum class ValidationResult {
    Success, ErrorNoSite, ErrorNoTank, ErrorNoIndicators, ErrorNoFeedProducer, ErrorNoFeedCoef, ErrorNoFeedPeriodFrom,
    ErrorNoFeedPeriodTo, ErrorNoMovingDestinationSite, ErrorNoMovingDestinationTank, ErrorNoRecord
}

class TroutNewRecordValidator : NewRecordValidator {
    private var sites: List<Site>? = null
    private var tanks: List<Tank>? = null
    private var record: Record? = null

    override fun validateRecord(): ValidationResult {
        record?.let {record ->
            loge("$record")

            if(record.site == null) {
                return ValidationResult.ErrorNoSite
            }

            if(record.tank == null) {
                return ValidationResult.ErrorNoTank
            }

            if(record.temperatureIndicator == null && record.oxygenIndicator == null && record.feedingIndicator == null
                && record.fishIndicator == null) {
                return ValidationResult.ErrorNoIndicators
            }

            if(record.feedingIndicator != null) {
                if (record.feedProducer == null) {
                    return ValidationResult.ErrorNoFeedProducer
                }
                if(record.feedCoef == null) {
                    return ValidationResult.ErrorNoFeedCoef
                }

                if(record.feedPeriodFrom != null && record.feedPeriodTo == null) {
                    return ValidationResult.ErrorNoFeedPeriodTo
                }

                if(record.feedPeriodTo != null && record.feedPeriodFrom == null) {
                    return ValidationResult.ErrorNoFeedPeriodFrom
                }
            }

            if(record.fishIndicator != null && record.fishIndicatorType == IndicatorType.Moving) {
                if(record.fishMovingDestinationSite == null) {
                    return ValidationResult.ErrorNoMovingDestinationSite
                }
                if(record.fishMovingDestinationTank == null) {
                    return ValidationResult.ErrorNoMovingDestinationTank
                }
            }

            return ValidationResult.Success
        }

        return ValidationResult.ErrorNoRecord
    }

    override fun validateTankNumber(tankNumber: Int): Boolean {
        val siteId = record?.site?.id
        siteId?.let { id ->
            val tank = tanks?.find { tank ->
                tank.siteId == id && tank.number == tankNumber
            }

            if(tank != null) {
                return true
            }
        }

        return false
    }

    override fun validateDestinationTankNumber(tankNumber: Int): Boolean {
        val siteId = record?.fishMovingDestinationSite?.id
        siteId?.let { id ->
            val tank = tanks?.find { tank ->
                tank.siteId == id && tank.number == tankNumber
            }

            if(tank != null) {
                return true
            }
        }

        return false
    }

    override fun setSites(sites: List<Site>?) {
        this.sites = sites
    }

    override fun setAllTanks(tanks: List<Tank>?) {
        this.tanks = tanks
    }

    override fun setRecord(record: Record?) {
        this.record = record
    }

    override fun getSites(): List<Site>? {
        return this.sites
    }
}

class EmptyNewRecordValidator : NewRecordValidator {
    override fun validateRecord(): ValidationResult {
        return ValidationResult.Success
    }

    override fun validateTankNumber(tankNumber: Int): Boolean {
        return true
    }

    override fun validateDestinationTankNumber(tankNumber: Int): Boolean {
        return true
    }

    override fun setSites(sites: List<Site>?) {}

    override fun setAllTanks(tanks: List<Tank>?) {}

    override fun setRecord(record: Record?) {}

    override fun getSites(): List<Site>? = null
}
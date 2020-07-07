package ru.arvata.pomor.ui.newrecord

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import ru.arvata.pomor.data.*
import ru.arvata.pomor.ui.*
import ru.arvata.pomor.util.*
import java.lang.Exception

class NewRecordViewModel : ViewModel() {
    private val _recordLiveData = MutableLiveData<Record>()
    private var record: Record = resetRecord()

    val sitesDisplay = LocalRepository.sitesLiveData.map {
        val result = mutableListOf("Выберите участок")

        result += it.map { site ->
            getSiteDisplay(site)
        }

        result.toTypedArray()
    }

    private val tanksChanged = SingleLiveEvent<Void>()
    private val destinationTanksChanged = SingleLiveEvent<Void>()

    val siteTanksDisplayLiveData: LiveData<Array<String>?> = combine2(LocalRepository.allTanksLiveData, tanksChanged) { tanks: List<Tank>?, changed: Void? ->
        if(tanks == null) {
            return@combine2 null
        }

        val result = mutableListOf("Выберите садок")

        record.site?.let { site ->
            result.addAll(
                tanks.filter { tank ->
                    tank.siteId == site.id
                }.map {
                    getTankNumberDisplay(it)
                }
            )
        }

        result.toTypedArray()
    }

    val destinationTanksDisplayLiveData: LiveData<Array<String>?> = combine2(LocalRepository.allTanksLiveData,
        destinationTanksChanged) { tanks: List<Tank>?, changed: Void? ->
        if(tanks == null) {
            return@combine2 null
        }

        val result = mutableListOf("Выберите садок")

        record.fishMovingDestinationSite?.let { site ->
            result.addAll(
                tanks.filter { tank ->
                    tank.siteId == site.id
                }.map {
                    getTankNumberDisplay(it)
                }
            )
        }

        result.toTypedArray()
    }

    val feedProducerLiveData: LiveData<Array<String>> = LocalRepository.feedProducersLiveData.map { list ->
        val result = mutableListOf("Выберите марку корма")
        result += list.map { feedProducer ->
            getFeedProducerDisplay(feedProducer)
        }
        result.toTypedArray()
    }

    val validator = TroutNewRecordValidator()

    val tabsChangedLiveData: LiveData<TabsChanged> = Transformations.map(_recordLiveData) {
        val indicatorsChanged = it?.temperatureIndicator != null || it?.oxygenIndicator != null
        val feedingChanged = it?.feedingIndicator != null
        val fishChanged = it?.fishIndicator != null
        TabsChanged(indicatorsChanged, feedingChanged, fishChanged)
    }

    val recordDisplayLiveData: LiveData<RecordDisplay> = _recordLiveData.map {
            RecordDisplay(
                it.temperatureIndicator != null, getHhmm_ddMMyyyyString(it.temperatureTime),
                it.oxygenIndicator != null, getHhmm_ddMMyyyyString(it.oxygenTime),
                it.feedingIndicator != null, getHhmm_ddMMyyyyString(it.feedingTime),
                it.feedProducer != null, it.feedCoef != null,
                it.feedPeriodFrom != null, it.feedPeriodTo != null,
                true, it.fishIndicator != null, getHhmm_ddMMyyyyString(it.fishIndicatorTime),
                it.fishMovingDestinationSite != null, it.fishMovingDestinationTank != null,
                it.fishMovingReason != null,
                it.fishCatchReason != null
            )
    }

    init {
        resetRecord()

        LocalRepository.sitesLiveData.observeForever {
            validator.setSites(it)
        }

        LocalRepository.allTanksLiveData.observeForever {
            validator.setAllTanks(it)
        }

        _recordLiveData.observeForever {
            validator.setRecord(it)
        }
    }

    fun setSite(position: Int) {
        if (position == 0) {
            record.site = null
        } else {
            val site = LocalRepository.sitesLiveData.value?.get(position - 1)
            record.site = site
        }

        _recordLiveData.value = record
        tanksChanged.call()
    }

    fun getSitePosition(site: Site?): Int {
        val index = LocalRepository.sitesLiveData.value?.indexOfFirst {
            it.id == site?.id
        }
        return if(index == null || index == -1) {
            0
        } else {
            index + 1
        }
    }

    fun setTank(position: Int) {
        if (position == 0 || record.site == null) {
            record.tank = null
        } else {
            val tank = LocalRepository.allTanksLiveData.value?.filter {
                it.siteId == record.site?.id
            }?.get(position - 1)
            record.tank = tank
        }

        _recordLiveData.value = record
    }

    fun setTemperature(value: String?) {
        record.temperatureIndicator = try {
            value?.toFloat()
        } catch (e: Exception) {
            null
        }
        _recordLiveData.value = record
    }

    fun setTemperatureTime(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val iso = getIsoDateTimeString(year, month, day, hour, minute)
        iso?.let {
            record.temperatureTime = iso
            _recordLiveData.value = record
        }
    }

    fun setOxygen(value: String?) {
        record.oxygenIndicator = try {
            value?.toFloat()
        } catch (e: Exception) {
            null
        }

        _recordLiveData.value = record
    }

    fun setOxygenTime(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val iso = getIsoDateTimeString(year, month, day, hour, minute)
        iso?.let {
            record.oxygenTime = iso
            _recordLiveData.value = record
        }
    }

    fun setFeeding(value: String?) {
        record.feedingIndicator = try {
            value?.toFloat()
        } catch (e: Exception) {
            null
        }

        _recordLiveData.value = record
    }

    fun setFeedingTime(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val iso = getIsoDateTimeString(year, month, day, hour, minute)
        iso?.let {
            record.feedingTime = iso
            _recordLiveData.value = record
        }
    }

    fun getFeedProducerPosition(feedProducerType: FeedProducerType): Int {
        return if(feedProducerType == FeedProducerType.Biomar) {
            1
        } else {
            2
        }
    }

    fun setFeedProducer(position: Int) {
        if(position == 0) {
            record.feedProducer = null
        } else {
            record.feedProducer = LocalRepository.feedProducersLiveData.value?.get(position - 1)
        }

        _recordLiveData.value = record
    }

    fun setFeedCoef(value: String?) {
        record.feedCoef = try {
            value?.toFloat()
        } catch (e: Exception) {
            null
        }

        _recordLiveData.value = record
    }

    fun setFeedPeriodFrom(date: ddMMyyyy?) {
        record.feedPeriodFrom = date
        _recordLiveData.value = record
    }

    fun setFeedPeriodTo(date: ddMMyyyy?) {
        record.feedPeriodTo = date
        _recordLiveData.value = record
    }

    fun setFishIndicatorType(position: Int): IndicatorType? {
        record.fishIndicatorType = when (position) {
            0 -> IndicatorType.Mortality
            1 -> IndicatorType.Seeding
            2 -> IndicatorType.Moving
            3 -> IndicatorType.Weight
            4 -> IndicatorType.Catch
            else -> null
        }

        _recordLiveData.value = record
        return record.fishIndicatorType
    }

    fun setFishIndicator(value: String?) {
        var indicator: Number? = value?.toIntOrNull()
        if(indicator == null) {
            indicator = value?.toFloatOrNull()
        }

        record.fishIndicator = indicator
        _recordLiveData.value = record
    }

    fun setFishTime(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val iso = getIsoDateTimeString(year, month, day, hour, minute)
        iso?.let {
            record.fishIndicatorTime = iso
            _recordLiveData.value = record
        }
    }

    fun setFishMovingDestinationSite(position: Int) {
        if(position == 0) {
            record.fishMovingDestinationSite = null
        } else {
            record.fishMovingDestinationSite = LocalRepository.sitesLiveData.value?.get(position - 1)
        }

        _recordLiveData.value = record
        destinationTanksChanged.call()
    }

    fun setFishMovingDestinationTank(position: Int) {
        if(position == 0) {
            record.fishMovingDestinationTank = null
        } else {
            val tank = LocalRepository.allTanksLiveData.value?.filter {
                it.siteId == record.fishMovingDestinationSite?.id
            }?.get(position - 1)

            record.fishMovingDestinationTank = tank
        }

        _recordLiveData.value = record
    }

    fun setFishMovingReason(value: String?) {
        record.fishMovingReason = value
        _recordLiveData.value = record
    }

    fun setCatchReason(value: String?) {
        record.fishCatchReason = value
        _recordLiveData.value = record
    }

    private fun resetRecord(): Record {
        val currentTime = getCurrentTime()
        val record = Record(null, null, null, currentTime, null, currentTime,
            null, currentTime, null, null, null, null,
            null, null, currentTime, null, null,
            null, null)

        _recordLiveData.value = record
        return record
    }

    fun cancelRecord() {
        resetRecord()
    }

    fun validateRecord(): ValidationResult {
        return validator.validateRecord()
    }

    fun confirmRecord() {
        val list = convertRecord(record)
        if(list != null) {
            LocalRepository.saveIndicatorsList(UserRepository.loggedUserLiveData.value?.id, list)
        }
        this.record = resetRecord()
    }

    private fun convertRecord(record: Record): List<Indicator>? {
        with(record) {
            tank?.let {tank ->
                val temperature = temperatureIndicator?.let {
                    Indicator(getIndicatorId(temperatureTime, IndicatorType.Temperature),
                        it, temperatureTime, tank, IndicatorType.Temperature)
                }

                val oxygen = oxygenIndicator?.let {
                    Indicator(getIndicatorId(oxygenTime, IndicatorType.Oxygen),
                        it, oxygenTime, tank, IndicatorType.Oxygen)
                }

                val feeding = let2(feedingIndicator, feedProducer) {
                        feedingIndicator: Float, feedProducer: FeedProducer ->
                    feedCoef?.let { coef ->
                        Indicator(getIndicatorId(feedingTime, IndicatorType.Feeding),
                            feedingIndicator, feedingTime, tank, IndicatorType.Feeding,
                            feedProducer = feedProducer, feedCoef = coef,
                            feedPeriodFrom = feedPeriodFrom, feedPeriodTo = feedPeriodTo)
                    }
                }

                val fishIndicator = let2(fishIndicator, fishIndicatorType) {
                        indicator: Number, indicatorType: IndicatorType ->

                    val movingDestination = if(indicatorType == IndicatorType.Moving) {
                        fishMovingDestinationTank
                    } else null

                    val movingReason = if(indicatorType == IndicatorType.Moving) {
                        fishMovingReason
                    } else null

                    val catchReason = if(indicatorType == IndicatorType.Catch) {
                        fishCatchReason
                    } else null

                    Indicator(getIndicatorId(fishIndicatorTime, indicatorType),
                        indicator, fishIndicatorTime,
                        tank, indicatorType,
                        movingTank = movingDestination, movingReason = movingReason,
                        catchReason = catchReason)
                }
                return listOfNotNull(temperature, oxygen, feeding, fishIndicator)
            }
        }
        return null
    }
}

data class Record(var site: Site?,
                  var tank: Tank?,
                  var temperatureIndicator: Float?,
                  var temperatureTime: isoDateTimeString,
                  var oxygenIndicator: Float?,
                  var oxygenTime: isoDateTimeString,
                  var feedingIndicator: Float?,
                  var feedingTime: isoDateTimeString,
                  var feedProducer: FeedProducer?,
                  var feedCoef: Float?,
                  var feedPeriodFrom: ddMMyyyy?,
                  var feedPeriodTo: ddMMyyyy?,
                  var fishIndicatorType: IndicatorType?,
                  var fishIndicator: Number?,
                  var fishIndicatorTime: isoDateTimeString,
                  var fishMovingDestinationSite: Site?,
                  var fishMovingDestinationTank: Tank?,
                  var fishMovingReason: String?,
                  var fishCatchReason: String?)

data class RecordDisplay(val temperatureIndicator: Boolean, val temperatureTime: HHmm_ddMMyyyy,
                         val oxygenIndicator: Boolean, val oxygenTime: HHmm_ddMMyyyy,
                         val feedingIndicator: Boolean, val feedingTime: HHmm_ddMMyyyy,
                         val feedProducer: Boolean, val feedCoef: Boolean,
                         val feedPeriodFrom: Boolean, val feedPeriodTo: Boolean,
                         val fishIndicatorType: Boolean, val fishIndicator: Boolean,
                         val fishIndicatorTime: HHmm_ddMMyyyy, val fishMovingDestinationSite: Boolean,
                         val fishMovingDestinationTank: Boolean,
                         val fishMovingReason: Boolean, val fishCatchReason: Boolean)

data class TabsChanged(val indicatorsChanged: Boolean, val feedingChanged: Boolean, val fishChanged: Boolean)
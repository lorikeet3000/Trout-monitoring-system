package ru.arvata.pomor.ui.mainpage

import android.arch.lifecycle.*
import ru.arvata.pomor.data.*
import ru.arvata.pomor.ui.*
import ru.arvata.pomor.util.*

class TankInfoViewModel : ViewModel() {
    val tanksDisplayLiveData: LiveData<List<TankDisplay>?> = LocalRepository.siteTanksLiveData.map { list: List<Tank>? ->
        list?.map {tank ->
            TankDisplay(tank.number.toString(), getTankColor(tank))
        }
    }

    private val _selectedTankLiveData = MutableLiveData<Tank>()

    private val _selectedPeriod = MutableLiveData<Period>()

    val selectedTankDisplayLiveData: LiveData<TankDisplay> = _selectedTankLiveData.map { tank ->
        TankDisplay(tank.number.toString(), getTankColor(tank))
    }

    val tankInfoLiveData: LiveData<TankInfoDisplay> = _selectedTankLiveData.map {tank ->
        TankInfoDisplay(
            getTankNumberDisplay(tank),
            getFishWeightDisplay(tank.fishWeight?.indicator as? Float),
            getFishAmountDisplay(tank.fishAmount),
            getAllWeightDisplay(tank.allWeight()),
            getTankColor(tank))
    }

    val indicatorsLiveData: LiveData<IndicatorsDisplay> = _selectedTankLiveData.map {tank ->
        IndicatorsDisplay(
            getTemperatureIndicatorDisplay(tank),
            getTemperatureColor(tank),
            getRelativeDateTimeString(tank.temperature?.time ?: UNKNOWN_TIME),
            getOxygenIndicatorDisplay(tank),
            getOxygenColor(tank),
            getRelativeDateTimeString(tank.oxygen?.time ?: UNKNOWN_TIME)
        )
    }

    val feedingLiveData: LiveData<FeedingDisplay> = _selectedTankLiveData.map {tank ->
        FeedingDisplay(
            getFeedingIndicatorDisplay(tank),
            getRelativeDateTimeString(tank.feeding?.time ?: UNKNOWN_TIME),
            getFeedingColor(),
            getFeedProducerDisplay(tank.feeding?.feedProducer)
        )
    }

    val fishLiveData: LiveData<FishDisplay?> = combine3(_selectedTankLiveData, LocalRepository.sitesLiveData,
        LocalRepository.allTanksLiveData) {tank: Tank?, sites: List<Site>?, allTanks: List<Tank>? ->
        if(tank == null) {
            return@combine3 null
        }

        val movingTankId = tank.moving?.movingTankId
        val destinationTank = allTanks?.find {
            movingTankId == it.id
        }
        val destinationSite = sites?.find {
            destinationTank?.siteId == it.id
        }

        FishDisplay(
            getMortalityIndicatorDisplay(tank),
            getRelativeDateTimeString(tank.mortality?.time ?: UNKNOWN_TIME),
            getMortalityColor(),
            getSeedingIndicatorDisplay(tank),
            getRelativeDateTimeString(tank.seeding?.time ?: UNKNOWN_TIME),
            getSeedingColor(),
            getMovingIndicatorDisplay(tank),
            getRelativeDateTimeString(tank.moving?.time ?: UNKNOWN_TIME),
            getTankWithSiteDisplay(destinationTank, destinationSite),
            getMovingColor(),
            getCatchIndicatorDisplay(tank),
            getRelativeDateTimeString(tank.catch?.time ?: UNKNOWN_TIME),
            getCatchColor()
        )
    }

    val temperatureTableLiveData: LiveData<TableDisplay?> = combine3(LocalRepository.temperatureIndicatorsLiveData,
        _selectedTankLiveData, _selectedPeriod) { indicators: List<Indicator>?, tank: Tank?, period: Period? ->
        val list = getIndicatorsList(indicators, tank?.id, period)
        getTableDisplay(list, ::getTemperatureIndicatorDisplay)
    }

    val temperatureChartLiveData: LiveData<ChartDisplay?> = combine3(LocalRepository.temperatureIndicatorsLiveData,
        _selectedTankLiveData, _selectedPeriod) { indicators: List<Indicator>?, tank: Tank?, period: Period? ->
        val list = getIndicatorsList(indicators, tank?.id, period)
        getChartDisplay(list, ::getTemperatureIndicatorDisplay, ::getTemperatureColor, TemperatureConstants)
    }

    val oxygenTableLiveData: LiveData<TableDisplay?> = combine3(LocalRepository.oxygenIndicatorsLiveData,
        _selectedTankLiveData, _selectedPeriod) { indicators: List<Indicator>?, tank: Tank?, period: Period? ->
        val list = getIndicatorsList(indicators, tank?.id, period)
        getTableDisplay(list, ::getOxygenIndicatorDisplay)
    }

    val oxygenChartLiveData: LiveData<ChartDisplay?> = combine3(LocalRepository.oxygenIndicatorsLiveData,
        _selectedTankLiveData, _selectedPeriod) { indicators: List<Indicator>?, tank: Tank?, period: Period? ->
        val list = getIndicatorsList(indicators, tank?.id, period)
        getChartDisplay(list, ::getOxygenIndicatorDisplay, ::getOxygenColor, OxygenConstants)
    }

    fun getTableLiveData(type: ChartType): LiveData<TableDisplay?> {
        return when (type) {
            ChartType.Temperature -> temperatureTableLiveData
            ChartType.Oxygen -> oxygenTableLiveData
        }
    }

    fun getChartLiveData(type: ChartType): LiveData<ChartDisplay?> {
        return when (type) {
            ChartType.Temperature -> temperatureChartLiveData
            ChartType.Oxygen -> oxygenChartLiveData
        }
    }

    fun selectTank(tankNumber: Int): Boolean {
        val tank = LocalRepository.siteTanksLiveData.value?.find {
            tankNumber == it.number
        }

        //todo можно выбрать садок, переключиться на участок, где нет садка с таким номером, но в bottom sheet он все равно останется
        return if(tank != null) {
            _selectedTankLiveData.value = tank
            true
        } else {
            false
        }
    }

    fun selectPrevTank() {
        val currentTankNumber = _selectedTankLiveData.value?.number ?: -1
        selectTank(currentTankNumber - 1)
    }

    fun selectNextTank() {
        val currentTankNumber = _selectedTankLiveData.value?.number ?: -1
        selectTank(currentTankNumber + 1)
    }

    fun isTankSelected(): Boolean = _selectedTankLiveData.value != null

    fun selectPeriod(position: Int) {
        val period = when (position) {
            0 -> Period.Day
            1 -> Period.ThreeDays
            2 -> Period.SevenDays
            else -> Period.SevenDays
        }
        _selectedPeriod.value = period
    }

    private fun getIndicatorsList(list: List<Indicator>?, tankId: String?, period: Period?): List<Indicator>? {
        return list?.filter { indicator ->
            tankId != null && (tankId == indicator.tank.id)
        }?.filter {
            if(period == null) {
                true
            } else {
                val days = when (period) {
                    Period.Day -> 1
                    Period.ThreeDays -> 3
                    Period.SevenDays -> 7
                }
                isDateInPeriod(days * 24, it.time)
            }
        }?.sortedBy { indicator ->
            indicator.time
        }
    }

    private fun getTableDisplay(indicators: List<Indicator>?,
                                indicatorDisplayFunction: IndicatorDisplayFunction): TableDisplay {
        if(indicators == null || indicators.isEmpty()) {
            return TableDisplay(null)
        }

        val indicatorsReversed = indicators.reversed()

        val diffList = mutableListOf<Diff>().apply {
            addAll(
                indicatorsReversed.zipWithNext { new, old ->
                    val value = (new.indicator as? Float ?: 0.0f) - (old.indicator as? Float ?: 0.0f)
                    val color = TankColor.Unknown
                    Diff(value, color)
                }
            )
            add(Diff(0.0f, TankColor.Unknown))
        }

        val tableRows = indicatorsReversed
            .zip(diffList)
            .map {pair ->
                TableRowDisplay(
                    getHhmmString(pair.first.time),
                    getDdMMyyyyString(pair.first.time),
                    indicatorDisplayFunction(pair.first.indicator as? Float),
                    getDiffDisplay(pair.second.value),
                    pair.second.color)
            }

        return TableDisplay(tableRows)
    }

    private fun getChartDisplay(indicators: List<Indicator>?,
                                indicatorDisplayFunction: IndicatorDisplayFunction,
                                colorFunction: ColorFunction,
                                constants: IndicatorsConstants): ChartDisplay {
        if(indicators == null || indicators.isEmpty()) {
            return ChartDisplay(UNKNOWN_TIME, indicatorDisplayFunction(null), colorFunction(null as? Float),
                constants,null, null)
        }

        val chartPoints = indicators.mapIndexed { i, indicator ->
            ChartPoint(i.toFloat(), indicator.indicator as? Float ?: 0.0f)
        }

        val firstIndicatorTime = indicators.first().time
        val lastIndicatorTime = indicators.last().time

        val lastIndicator = indicatorDisplayFunction(indicators.last().indicator as? Float)
        val lastIndicatorColor = colorFunction(indicators.last().indicator as? Float)
        val xLabels = calculatePeriodTimePointsDisplay(firstIndicatorTime, lastIndicatorTime)

        return ChartDisplay(
            getRelativeDateTimeString(lastIndicatorTime),
            lastIndicator,
            lastIndicatorColor,
            constants,
            chartPoints,
            xLabels
        )
    }

    private fun calculatePeriodTimePointsDisplay(firstPoint: isoDateTimeString,
                                                 lastPoint: isoDateTimeString): List<String> {
        /*
            считает отметки времени, расположенные между заданными двумя
         */
        return listOf(
            getShortDateTimeString(firstPoint),
            getShortDateTimeString(getTimePointBetween(firstPoint, lastPoint, 0.5f)),
            getShortDateTimeString(lastPoint)
        )
    }
}

data class TankInfoDisplay(val tankNumber: String?, val weight: String, val amount: String, val allWeight: String,
                           val tankColor: TankColor)

data class IndicatorsDisplay(val temperatureIndicator: String, val temperatureColor: TankColor, val temperatureTime: String,
                             val oxygenIndicator: String, val oxygenColor: TankColor, val oxygenTime: String)

data class FeedingDisplay(val feedingIndicator: String, val feedingTime: String, val feedingColor: TankColor, val feedProducer: String)

data class FishDisplay(val mortalityIndicator: String, val mortalityTime: String, val mortalityColor: TankColor,
                       val seedingIndicator: String, val seedingTime: String, val seedingColor: TankColor,
                       val movingIndicator: String, val movingTime: String, val movingDestination: String, val movingColor: TankColor,
                       val catchIndicator: String, val catchTime: String, val catchColor: TankColor)

data class TankDisplay(val tankNumber: String, val tankColor: TankColor)

data class TableDisplay(val rows: List<TableRowDisplay>?)

data class TableRowDisplay(val time: HHmm, val date: ddMMyyyy, val indicator: String, val diff: String, val diffColor: TankColor)

data class ChartDisplay(val indicatorTime: relativeDateTimeString, val indicator: String, val indicatorColor: TankColor,
                        val indicatorsConstants: IndicatorsConstants,
                        val chartPointList: List<ChartPoint>?, val chartXLabels: List<String>?)

data class ChartPoint(val x: Float, val y: Float)

data class Diff(val value: Float, val color: TankColor)

enum class Period {
    Day, ThreeDays, SevenDays
}
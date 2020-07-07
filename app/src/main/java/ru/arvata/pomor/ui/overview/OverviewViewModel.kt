package ru.arvata.pomor.ui.overview

import android.arch.lifecycle.ViewModel
import ru.arvata.pomor.data.*
import ru.arvata.pomor.ui.*
import ru.arvata.pomor.util.*

class OverviewViewModel : ViewModel() {
    val selectedSiteLiveData = LocalRepository.selectedSiteLiveData

    val siteLiveData = combine2(selectedSiteLiveData, LocalRepository.siteTanksLiveData) {
            selectedSite: Site?, tanks: List<Tank>? ->

        val tanksNumber = tanks?.size ?: 0
        var fishAmount = 0
        var allWeight = 0.0f
        var sumTemperature = 0.0f
        var sumOxygen = 0.0f

        var lastTemperatureIndicator = if(tanks != null && tanks.isNotEmpty()) tanks[0].temperature else null

        var lastOxygenIndicator = if(tanks != null && tanks.isNotEmpty()) tanks[0].oxygen else null

        if(tanks != null && tanks.isNotEmpty()) {
            for (tank in tanks) {
                fishAmount += tank.fishAmount
                allWeight += tank.allWeight()
                sumTemperature += tank.temperature?.indicator as? Float ?: 0.0f
                sumOxygen += tank.oxygen?.indicator as? Float ?: 0.0f
                if ((tank.temperature?.time ?: "") > (lastTemperatureIndicator?.time ?: "")) {
                    lastTemperatureIndicator = tank.temperature
                }
                if ((tank.oxygen?.time ?: "") > (lastOxygenIndicator?.time ?: "")) {
                    lastOxygenIndicator = tank.oxygen
                }
            }
        }

        SiteDisplay(tanksNumber.toString(), getFishAmountDisplay(fishAmount), getAllWeightDisplay(allWeight),
            getTemperatureIndicatorDisplay(lastTemperatureIndicator?.indicator as? Float),
            getRelativeDateTimeString(lastTemperatureIndicator?.time ?: UNKNOWN_TIME),
            getTemperatureColor(lastTemperatureIndicator?.indicator as? Float),
            getOxygenIndicatorDisplay(lastOxygenIndicator?.indicator as? Float),
            getRelativeDateTimeString(lastOxygenIndicator?.time ?: UNKNOWN_TIME),
            getOxygenColor(lastOxygenIndicator?.indicator as? Float)
        )
    }

    val areaLiveData = combine3(selectedSiteLiveData, LocalRepository.sitesLiveData, LocalRepository.siteTanksLiveData) {
            selectedSite: Site?, sites: List<Site>?, tanks: List<Tank>? ->
        if(selectedSite != null || sites == null || tanks == null) {
            return@combine3 null
        }

        val sitesNumber = sites.size
        val tanksNumber = tanks.size
        val fishAmount = tanks.sumBy {
            it.fishAmount
        }
        val allWeight = tanks.sumByDouble {
            it.allWeight().toDouble()
        }.toFloat()

        AreaDisplay(sitesNumber.toString(), tanksNumber.toString(),
            getFishAmountDisplay(fishAmount),
            getAllWeightDisplay(allWeight))
    }
}

data class SiteDisplay(val tanksNumber: String, val fishAmount: String, val allWeight: String,
                       val avgTemperatureIndicator: String, val lastTemperatureTime: relativeDateTimeString,
                       val avgTemperatureColor: TankColor,
                       val avgOxygenIndicator: String, val lastOxygenTime: relativeDateTimeString,
                       val avgOxygenColor: TankColor)

data class AreaDisplay(val sitesNumber: String, val tanksNumber: String, val fishAmount: String, val allWeight: String)
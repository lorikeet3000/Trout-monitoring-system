package ru.arvata.pomor.ui.mainpage

import android.arch.lifecycle.ViewModel
import ru.arvata.pomor.data.LocalRepository
import ru.arvata.pomor.data.Site
import ru.arvata.pomor.data.Tank
import ru.arvata.pomor.ui.TankColor
import ru.arvata.pomor.ui.getTankColor
import ru.arvata.pomor.util.combine2

class SitesViewModel : ViewModel() {
    val sitesLiveData = combine2(LocalRepository.sitesLiveData,
        LocalRepository.allTanksLiveData) { sites: List<Site>?, allTanks: List<Tank>? ->
        if(sites == null || allTanks == null) {
            return@combine2 null
        }
        sites.map {site ->

            val tanks = allTanks.filter {
                it.siteId == site.id
            }
            val tanksNumber = tanks.size
            var greenTanks = 0
            var yellowTanks = 0
            var redTanks = 0

            for (tank in tanks) {
                val tankColor = getTankColor(tank)
                when (tankColor) {
                    TankColor.Green -> greenTanks++
                    TankColor.Yellow -> yellowTanks++
                    TankColor.Red -> redTanks++
                }
            }

            val siteColor = if(redTanks > 0) {
                TankColor.Red
            } else if(yellowTanks > 0) {
                TankColor.Yellow
            } else if(greenTanks > 0) {
                TankColor.Green
            } else {
                TankColor.Unknown
            }

            SiteDisplay(site.name, siteColor, tanksNumber, greenTanks.toString(), yellowTanks.toString(),
                redTanks.toString())
        }
    }
}

data class SiteDisplay(val name: String, val color: TankColor, val tanksNumber: Int, val greenNumber: String,
                       val yellowNumber: String, val redNumber: String)
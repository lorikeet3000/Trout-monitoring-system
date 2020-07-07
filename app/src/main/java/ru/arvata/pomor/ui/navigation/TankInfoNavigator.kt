package ru.arvata.pomor.ui.navigation

import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import ru.arvata.pomor.R
import ru.arvata.pomor.ui.mainpage.*

object TankInfoNavigator {
    const val TANK_INFO_INDICATORS = "tank_info_indicators"
    const val TANK_INFO_FEEDING = "tank_info_feeding"
    const val TANK_INFO_FISH = "tank_info_fish"
    const val TANK_INFO_BOTTOM = "tank_info_bottom"
    const val TANK_INFO_TEMPERATURE = "tank_info_temperature"
    const val TANK_INFO_OXYGEN = "tank_info_oxygen"

    fun showIndicatorsTab(fragmentManager: FragmentManager) {
        var fragment = fragmentManager.findFragmentByTag(TANK_INFO_INDICATORS) as? TankInfoIndicatorsFragment
        if(fragment == null) {
            fragment = TankInfoIndicatorsFragment.newInstance()
        }
        fragmentManager.beginTransaction().apply {
            replace(R.id.tab_container, fragment, TANK_INFO_INDICATORS)
        }.commit()
    }

    fun showFeedingTab(fragmentManager: FragmentManager) {
        var fragment = fragmentManager.findFragmentByTag(TANK_INFO_FEEDING) as? TankInfoFeedingFragment
        if(fragment == null) {
            fragment = TankInfoFeedingFragment.newInstance()
        }
        fragmentManager.beginTransaction().apply {
            replace(R.id.tab_container, fragment, TANK_INFO_FEEDING)
        }.commit()
    }

    fun showFishTab(fragmentManager: FragmentManager) {
        var fragment = fragmentManager.findFragmentByTag(TANK_INFO_FISH) as? TankInfoFishFragment
        if(fragment == null) {
            fragment = TankInfoFishFragment.newInstance()
        }
        fragmentManager.beginTransaction().apply {
            replace(R.id.tab_container, fragment, TANK_INFO_FISH)
        }.commit()
    }

    fun navigateToTankInfo(activity: AppCompatActivity) {
        val mainFragment = activity.supportFragmentManager.findFragmentByTag(MainNavigator.MAIN_PAGE)
        mainFragment?.childFragmentManager?.beginTransaction()?.apply {
            replace(R.id.bottom_sheet_container,
                TankInfoFragment.newInstance(), TANK_INFO_BOTTOM)
        }?.commit()
    }

    fun navigateToTemperatureChart(activity: AppCompatActivity) {
        val mainFragment = activity.supportFragmentManager.findFragmentByTag(MainNavigator.MAIN_PAGE)
        mainFragment?.childFragmentManager?.beginTransaction()?.apply {
            replace(R.id.bottom_sheet_container,
                ChartFragment.newInstance(ChartType.Temperature), TANK_INFO_TEMPERATURE)
        }?.commit()
    }

    fun navigateToOxygenChart(activity: AppCompatActivity) {
        val mainFragment = activity.supportFragmentManager.findFragmentByTag(MainNavigator.MAIN_PAGE)
        mainFragment?.childFragmentManager?.beginTransaction()?.apply {
            replace(R.id.bottom_sheet_container,
                ChartFragment.newInstance(ChartType.Oxygen), TANK_INFO_OXYGEN)
        }?.commit()
    }
}
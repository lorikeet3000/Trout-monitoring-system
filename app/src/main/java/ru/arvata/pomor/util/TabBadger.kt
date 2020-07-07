package ru.arvata.pomor.util

import android.support.design.widget.TabLayout
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import ru.arvata.pomor.R
import ru.arvata.pomor.ui.newrecord.TabsChanged

class TabBadger(tabLayout: TabLayout, indicatorsTitle: String, feedingTitle: String, fishTitle: String) {
    private val indicatorsBadge: View
    private val feedingBadge: View
    private val fishBadge: View

    init {
        val indicatorsTab = LayoutInflater.from(tabLayout.context).inflate(R.layout.layout_new_record_tab, null, false)
        val feedingTab = LayoutInflater.from(tabLayout.context).inflate(R.layout.layout_new_record_tab, null, false)
        val fishTab = LayoutInflater.from(tabLayout.context).inflate(R.layout.layout_new_record_tab, null, false)

        indicatorsBadge = indicatorsTab.findViewById(R.id.badge)
        feedingBadge = feedingTab.findViewById(R.id.badge)
        fishBadge = fishTab.findViewById(R.id.badge)

        indicatorsTab.findViewById<TextView>(R.id.title).text = indicatorsTitle
        feedingTab.findViewById<TextView>(R.id.title).text = feedingTitle
        fishTab.findViewById<TextView>(R.id.title).text = fishTitle

        tabLayout.getTabAt(0)?.customView = indicatorsTab
        tabLayout.getTabAt(1)?.customView = feedingTab
        tabLayout.getTabAt(2)?.customView = fishTab

        indicatorsBadge.visibility = View.GONE
        feedingBadge.visibility = View.GONE
        fishBadge.visibility = View.GONE
    }

    fun setTabChanged(tabsChanged: TabsChanged) {
        indicatorsBadge.visibility = if(tabsChanged.indicatorsChanged) {
            View.VISIBLE
        } else {
            View.GONE
        }

        feedingBadge.visibility = if(tabsChanged.feedingChanged) {
            View.VISIBLE
        } else {
            View.GONE
        }

        fishBadge.visibility = if(tabsChanged.fishChanged) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}
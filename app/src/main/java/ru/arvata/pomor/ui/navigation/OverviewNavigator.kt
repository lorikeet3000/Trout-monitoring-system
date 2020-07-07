package ru.arvata.pomor.ui.navigation

import android.support.v4.app.FragmentManager
import ru.arvata.pomor.R
import ru.arvata.pomor.ui.overview.AreaOverviewFragment
import ru.arvata.pomor.ui.overview.SiteOverviewFragment

object OverviewNavigator {
    const val OVERVIEW_SITE = "overview_site"
    const val OVERVIEW_AREA = "overview_area"

    fun showSiteOverview(fragmentManager: FragmentManager) {
        var fragment = fragmentManager.findFragmentByTag(OVERVIEW_SITE) as? SiteOverviewFragment
        if(fragment == null) {
            fragment = SiteOverviewFragment.newInstance()
        }
        fragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment, OVERVIEW_SITE)
        }.commit()
    }

    fun showAreaOverview(fragmentManager: FragmentManager) {
        var fragment = fragmentManager.findFragmentByTag(OVERVIEW_AREA) as? AreaOverviewFragment
        if(fragment == null) {
            fragment = AreaOverviewFragment.newInstance()
        }
        fragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment, OVERVIEW_AREA)
        }.commit()
    }
}
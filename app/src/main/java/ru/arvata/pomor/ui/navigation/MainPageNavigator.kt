package ru.arvata.pomor.ui.navigation

import android.support.v4.app.FragmentManager
import ru.arvata.pomor.R
import ru.arvata.pomor.ui.mainpage.SitesFragment
import ru.arvata.pomor.ui.mainpage.TanksFragment

object MainPageNavigator {
    val MAIN_PAGE_SITES = "main_page_sites"
    val MAIN_PAGE_TANKS = "main_page_tanks"

    fun showSitesScreen(fragmentManager: FragmentManager) {
        var fragment = fragmentManager.findFragmentByTag(MAIN_PAGE_SITES)
        if(fragment == null) {
            fragment = SitesFragment.newInstance()
        }
        fragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment, MAIN_PAGE_SITES)
        }.commit()
    }

    fun showTanksScreen(fragmentManager: FragmentManager) {
        var fragment = fragmentManager.findFragmentByTag(MAIN_PAGE_TANKS)
        if(fragment == null) {
            fragment = TanksFragment.newInstance()
        }
        fragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment, MAIN_PAGE_TANKS)
        }.commit()
    }
}
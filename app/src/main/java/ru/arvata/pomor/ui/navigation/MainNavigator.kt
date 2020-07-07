package ru.arvata.pomor.ui.navigation

import android.support.v7.app.AppCompatActivity
import ru.arvata.pomor.R
import ru.arvata.pomor.ui.events.EventsFragment
import ru.arvata.pomor.ui.mainpage.MainPageFragment
import ru.arvata.pomor.ui.newrecord.NewRecordFragment
import ru.arvata.pomor.ui.overview.OverviewFragment
import ru.arvata.pomor.ui.plans.PlansFragment

object MainNavigator {
    val MAIN_PAGE = "main_page"
    val EVENT_PAGE = "event_page"
    val NEW_RECORD_PAGE = "new_record_page"
    val OVERVIEW_PAGE = "overview_page"
    val PLANS_PAGE = "plans_page"

    fun navigateToMainPage(activity: AppCompatActivity) {
        var fragment = activity.supportFragmentManager.findFragmentByTag(MAIN_PAGE)
        if(fragment == null) {
            fragment = MainPageFragment.newInstance()
        }
        activity.supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment, MAIN_PAGE)
        }.commit()
    }

    fun navigateToEvent(activity: AppCompatActivity) {
        var fragment = activity.supportFragmentManager.findFragmentByTag(EVENT_PAGE)
        if(fragment == null) {
            fragment = EventsFragment.newInstance()
        }
        activity.supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment, EVENT_PAGE)
        }.commit()
    }

    fun navigateToNewRecord(activity: AppCompatActivity) {
        var fragment = activity.supportFragmentManager.findFragmentByTag(NEW_RECORD_PAGE)
        if(fragment == null) {
            fragment = NewRecordFragment.newInstance()
        }
        activity.supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment, NEW_RECORD_PAGE)
        }.commit()
    }

    fun navigateToOverview(activity: AppCompatActivity) {
        var fragment = activity.supportFragmentManager.findFragmentByTag(OVERVIEW_PAGE)
        if(fragment == null) {
            fragment = OverviewFragment.newInstance()
        }
        activity.supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment, OVERVIEW_PAGE)
        }.commit()
    }

    fun navigateToPlans(activity: AppCompatActivity) {
        var fragment = activity.supportFragmentManager.findFragmentByTag(PLANS_PAGE)
        if(fragment == null) {
            fragment = PlansFragment.newInstance()
        }
        activity.supportFragmentManager.beginTransaction().apply {
            replace(R.id.container, fragment, PLANS_PAGE)
        }.commit()
    }
}
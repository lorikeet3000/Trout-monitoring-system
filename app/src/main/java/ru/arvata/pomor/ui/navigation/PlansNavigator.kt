package ru.arvata.pomor.ui.navigation

import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import ru.arvata.pomor.R
import ru.arvata.pomor.ui.plans.CreatePlanFragment
import ru.arvata.pomor.ui.plans.PlanInfoFragment
import ru.arvata.pomor.ui.plans.PlansListFragment

object PlansNavigator {
    const val PLANS_LIST = "plans_list"
    const val PLANS_CREATE = "plans_create"
    const val PLANS_INFO = "plans_info"

    fun navigateToPlansList(activity: AppCompatActivity) {
        val plansFragment = activity.supportFragmentManager.findFragmentByTag(MainNavigator.PLANS_PAGE)
        val plansList = plansFragment?.childFragmentManager?.findFragmentByTag(PLANS_LIST)
                as? PlansListFragment  ?: PlansListFragment.newInstance()
        plansFragment?.childFragmentManager?.beginTransaction()?.apply {
            replace(R.id.container, plansList, PLANS_LIST)
        }?.commit()
    }

    fun showCreatePlan(activity: AppCompatActivity, planId: String?) {
        val plansFragment = activity.supportFragmentManager.findFragmentByTag(MainNavigator.PLANS_PAGE)

        val createPlan = plansFragment?.childFragmentManager?.findFragmentByTag(PLANS_CREATE)
                as? CreatePlanFragment ?: CreatePlanFragment.newInstance(planId)
        plansFragment?.childFragmentManager?.beginTransaction()?.apply {
            replace(R.id.container, createPlan, PLANS_CREATE)
        }?.commit()
    }

    fun closeCreatePlan(activity: AppCompatActivity) {
        navigateToPlansList(activity)
    }

    fun showPlanInfo(activity: AppCompatActivity, planId: String) {
        val plansFragment = activity.supportFragmentManager.findFragmentByTag(MainNavigator.PLANS_PAGE)

        val planInfo = plansFragment?.childFragmentManager?.findFragmentByTag(PLANS_INFO)
            as? PlanInfoFragment ?: PlanInfoFragment.newInstance(planId)
        plansFragment?.childFragmentManager?.beginTransaction()?.apply {
            replace(R.id.container, planInfo, PLANS_INFO)
        }?.commit()
    }

    fun closePlanInfo(activity: AppCompatActivity) {
        navigateToPlansList(activity)
    }
}
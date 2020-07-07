package ru.arvata.pomor.ui.navigation

import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import ru.arvata.pomor.R
import ru.arvata.pomor.ui.newrecord.NewRecordFeedingFragment
import ru.arvata.pomor.ui.newrecord.NewRecordFishFragment
import ru.arvata.pomor.ui.newrecord.NewRecordIndicatorsFragment
import ru.arvata.pomor.util.DateTimePickerDialog

object NewRecordNavigator {
    const val NEW_RECORD_INDICATORS = "new_record_indicators"
    const val NEW_RECORD_FEEDING = "new_record_feeding"
    const val NEW_RECORD_FISH = "new_record_fish"
    const val NEW_RECORD_DATE_PICKER = "new_record_date_picker"

    fun showIndicatorsTab(fragmentManager: FragmentManager) {
        val transaction = fragmentManager.beginTransaction()

        if(fragmentManager.findFragmentByTag(NEW_RECORD_INDICATORS) == null) {
            transaction.add(R.id.tab_container,
                NewRecordIndicatorsFragment.newInstance(),
                NEW_RECORD_INDICATORS
            )
        }

        for (fragment in fragmentManager.fragments) {
            when(fragment.tag) {
                NEW_RECORD_INDICATORS -> transaction.show(fragment)
                NEW_RECORD_FEEDING, NEW_RECORD_FISH -> transaction.hide(fragment)
            }
        }

        transaction.commit()
    }

    fun showFeedingTab(fragmentManager: FragmentManager) {
        val transaction = fragmentManager.beginTransaction()

        if(fragmentManager.findFragmentByTag(NEW_RECORD_FEEDING) == null) {
            transaction.add(R.id.tab_container,
                NewRecordFeedingFragment.newInstance(),
                NEW_RECORD_FEEDING
            )
        }

        for (fragment in fragmentManager.fragments) {
            when(fragment.tag) {
                NEW_RECORD_FEEDING -> transaction.show(fragment)
                NEW_RECORD_INDICATORS, NEW_RECORD_FISH -> transaction.hide(fragment)
            }
        }

        transaction.commit()
    }

    fun showFishTab(fragmentManager: FragmentManager) {
        val transaction = fragmentManager.beginTransaction()

        if(fragmentManager.findFragmentByTag(NEW_RECORD_FISH) == null) {
            transaction.add(R.id.tab_container,
                NewRecordFishFragment.newInstance(),
                NEW_RECORD_FISH
            )
        }

        for (fragment in fragmentManager.fragments) {
            when(fragment.tag) {
                NEW_RECORD_FISH -> transaction.show(fragment)
                NEW_RECORD_INDICATORS, NEW_RECORD_FEEDING -> transaction.hide(fragment)
            }
        }

        transaction.commit()
    }

    fun showDateTimePickerDialog(activity: AppCompatActivity, listener: DateTimePickerDialog.OnDateTimeListener) {
        val newRecordFragment = activity.supportFragmentManager.findFragmentByTag(MainNavigator.NEW_RECORD_PAGE)
        newRecordFragment?.let {
            val tag = NEW_RECORD_DATE_PICKER

            if(it.childFragmentManager.findFragmentByTag(tag) == null) {
                val dialog = DateTimePickerDialog()
                dialog.setOnDateTimeListener(listener)
                dialog.show(it.childFragmentManager, tag)
            }
        }
    }
}
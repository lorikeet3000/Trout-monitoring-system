package ru.arvata.pomor.util

import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import ru.arvata.pomor.R
import java.util.*

@Suppress("DEPRECATION")
class DateTimePickerDialog : DialogFragment(), TabLayout.OnTabSelectedListener {
    private lateinit var pickerDate: DatePicker
    private lateinit var pickerTime: TimePicker
    private lateinit var tabLayout: TabLayout
    private var listener: OnDateTimeListener? = null
    private var selectedYear: Int
    private var selectedMonth: Int
    private var selectedDay: Int
    private var selectedHour: Int
    private var selectedMinute: Int

    init {
        val cal = Calendar.getInstance()
        selectedYear = cal[Calendar.YEAR]
        selectedMonth = cal[Calendar.MONTH]
        selectedDay = cal[Calendar.DAY_OF_MONTH]
        selectedHour = cal[Calendar.HOUR_OF_DAY]
        selectedMinute = cal[Calendar.MINUTE]
    }

    fun setOnDateTimeListener(listener: OnDateTimeListener?) {
        this.listener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_date_time_picker, null, false)

        pickerDate = view.findViewById(R.id.picker_date)
        val calendar = Calendar.getInstance()
        pickerDate.init(calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)) { datePicker: DatePicker, year: Int, month: Int, day: Int ->
            selectedYear = year
            selectedMonth = month
            selectedDay = day
        }

        pickerTime = view.findViewById(R.id.picker_time)
        pickerTime.setIs24HourView(true)
        pickerTime.setOnTimeChangedListener { timePicker: TimePicker, hour: Int, minute: Int ->
            selectedHour = hour
            selectedMinute = minute
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pickerTime.hour = selectedHour
            pickerTime.minute = selectedMinute
        } else {
            pickerTime.currentHour = selectedHour
            pickerTime.currentMinute = selectedMinute
        }

        tabLayout = view.findViewById(R.id.tabLayout)
        tabLayout.addOnTabSelectedListener(this)
        if(savedInstanceState == null) {
            tabLayout.getTabAt(0)?.select()
        }

        val builder = AlertDialog.Builder(activity!!).apply {
            setView(view)
            setPositiveButton(getString(R.string.apply)) { dialogInterface: DialogInterface, i: Int ->
                listener?.onDateTimeSet(selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute)
            }
            setNeutralButton(getString(R.string.cancel), null)
        }
        return builder.create()
    }

    private fun selectTab(position: Int) {
        if(position == 0) {
            pickerDate.visibility = View.INVISIBLE
            pickerTime.visibility = View.VISIBLE
        } else {
            pickerDate.visibility = View.VISIBLE
            pickerTime.visibility = View.INVISIBLE
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        onTabSelected(tab)
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if(tab != null) {
            selectTab(tab.position)
        }
    }

    override fun onPause() {
        listener = null
        dismiss()
        super.onPause()
    }

    interface OnDateTimeListener {
        fun onDateTimeSet(year: Int, month: Int, day: Int, hour: Int, minute: Int)
    }
}
package ru.arvata.pomor.ui.plans

import android.app.DatePickerDialog
import android.arch.lifecycle.LifecycleOwner
import android.view.View
import android.widget.DatePicker
import kotlinx.android.synthetic.main.layout_plans_filter.view.*
import ru.arvata.pomor.util.getDdMMyyyyString
import ru.arvata.pomor.util.init
import java.util.*

class PlansFilterView(private val container: View, private val viewLifecycleOwner: LifecycleOwner,
                      private val viewModel: PlansViewModel)     : DatePickerDialog.OnDateSetListener {
    private val datePickerDialog: DatePickerDialog

    init {
        val calendar = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(container.context, this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))

        container.apply {
            action_confirm.setOnClickListener {
                close()
            }

            action_reset_filters.setOnClickListener {
                selector_plan_type.setSelection(0)
                selector_plan_status.setSelection(0)
                selector_date.setSelection(0)
                removeDateFilter()
                close()
            }

            action_remove_date_filter.setOnClickListener {
                removeDateFilter()
            }

            selector_plan_type.init(arrayOf("Все", "Я исполнитель", "Я создатель")) { i: Int, s: String? ->
                viewModel.setFilterPlanType(i)
            }

            selector_plan_status.init(arrayOf("Все", "Выполненные", "Не выполненные")) { i: Int, s: String? ->
                viewModel.setFilterPlanStatus(i)
            }

            selector_date.init(arrayOf("Все", "Выбрать дату", "Сегодня", "Завтра")) { i: Int, s: String? ->
                viewModel.setFilterPlanDateType(i)
                container_date_picker.visibility = if(i == 1) View.VISIBLE else View.GONE
            }

            date_picker.setOnClickListener {
                datePickerDialog.show()
            }
        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

        val dateText = getDdMMyyyyString(calendar.time)
        container.date_picker.text = dateText

        viewModel.setFilterPlanDate(dateText)
    }

    fun close() {
        container.visibility = View.INVISIBLE
    }

    fun show() {
        container.visibility = View.VISIBLE
    }

    private fun removeDateFilter() {
        viewModel.setFilterPlanDate(null)
        container.date_picker.text = null
    }
}
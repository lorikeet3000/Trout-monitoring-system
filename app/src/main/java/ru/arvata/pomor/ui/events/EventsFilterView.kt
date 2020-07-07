package ru.arvata.pomor.ui.events

import android.app.DatePickerDialog
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.Observer
import android.view.View
import android.widget.DatePicker
import kotlinx.android.synthetic.main.layout_events_filter.view.*
import ru.arvata.pomor.util.getDdMMyyyyString
import ru.arvata.pomor.util.init
import java.util.Calendar

class EventsFilterView(private val container: View, private val viewLifecycleOwner: LifecycleOwner,
                       private val viewModel: EventsViewModel)
    : DatePickerDialog.OnDateSetListener {
    private val datePickerDialog: DatePickerDialog

    init {
        val calendar = Calendar.getInstance()
        datePickerDialog = DatePickerDialog(container.context, this,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))

        container.apply {
            action_confirm.setOnClickListener { close() }

            date_picker.setOnClickListener {
                datePickerDialog.show()
            }

            action_remove_date_filter.setOnClickListener {
                removeDateFilter()
            }

            action_reset_filters.setOnClickListener {
                selector_user.setSelection(0)
                selector_site.setSelection(0)
                selector_tank.setSelection(0)
                selector_date.setSelection(0)
                removeDateFilter()
                close()
            }

            selector_date.init(arrayOf("Все", "Выбрать дату", "Сегодня", "Вчера")) { i: Int, s: String? ->
                viewModel.setDateTypeFilter(i)
                container_date_picker.visibility = if(i == 1) View.VISIBLE else View.GONE
            }
        }

        viewModel.users.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                container.selector_user.init(it) { position: Int, text: String? ->
                    viewModel.setUserFilter(position, text)
                }
            }
        })

        viewModel.sites.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                container.selector_site.init(it) { position: Int, text: String? ->
                    viewModel.setSiteFilter(position, text)
                }
            }
        })

        viewModel.tanksDisplay.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                container.selector_tank.init(it) { position: Int, text: String? ->
                    viewModel.setTankFilter(position, text)
                }
            }
        })
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)

        val dateText = getDdMMyyyyString(calendar.time)
        container.date_picker.text = dateText

        viewModel.setDateFilter(dateText)
    }

    fun close() {
        container.visibility = View.INVISIBLE
    }

    fun show() {
        container.visibility = View.VISIBLE
    }

    private fun removeDateFilter() {
        viewModel.setDateFilter(null)
        container.date_picker.text = null
    }
}
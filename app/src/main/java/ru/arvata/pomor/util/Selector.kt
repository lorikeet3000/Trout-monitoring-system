package ru.arvata.pomor.util

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView

typealias SiteSelectorListener = (position: Int, text: String?) -> Unit

fun Spinner.init(data: Array<String>, listener: SiteSelectorListener?) {
    val adapter = ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, data)
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    this.adapter = adapter
    this.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {}

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            listener?.invoke(position, (view as? TextView)?.text?.toString())
        }
    }
    this.setSelection(0)
}
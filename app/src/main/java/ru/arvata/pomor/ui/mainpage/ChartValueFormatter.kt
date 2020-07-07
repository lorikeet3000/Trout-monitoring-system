package ru.arvata.pomor.ui.mainpage

import com.github.mikephil.charting.formatter.ValueFormatter

class ChartValueFormatter(private val labels: List<String>, private val maxValue: Int) : ValueFormatter() {

    override fun getFormattedValue(value: Float): String {
        if(value < 0.0f) {
            return ""
        }

        if(value == 0.0f) {
            return labels[0]
        }

        if(value == (maxValue - 1).toFloat()) {
            return labels.last()
        }

        return labels[1]
    }
}
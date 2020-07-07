package ru.arvata.pomor.ui.mainpage

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.android.synthetic.main.fragment_chart.*
import kotlinx.android.synthetic.main.layout_chart.*
import kotlinx.android.synthetic.main.layout_item_indicators_table.view.*

import ru.arvata.pomor.R
import ru.arvata.pomor.ui.navigation.TankInfoNavigator
import ru.arvata.pomor.ui.setIndicatorColor
import ru.arvata.pomor.ui.setTankColor
import ru.arvata.pomor.util.RecyclerViewAdapter
import ru.arvata.pomor.util.init
import xyz.sangcomz.stickytimelineview.RecyclerSectionItemDecoration
import xyz.sangcomz.stickytimelineview.model.SectionInfo
import java.lang.Exception

class ChartFragment : Fragment() {
    private lateinit var chartType: ChartType
    private lateinit var tankInfoViewModel: TankInfoViewModel
    private var recyclerViewAdapter: RecyclerViewAdapter<TableRowDisplay>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val typeString = arguments?.getString("type")

        chartType = if(typeString != null) {
            ChartType.valueOf(typeString)
        } else {
            ChartType.Temperature
        }

        tankInfoViewModel = ViewModelProviders.of(activity!!).get(TankInfoViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chart, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        container_chart.tag = "chart"
        recycler_view.tag = "table"

        when (chartType) {
            ChartType.Temperature -> {
                label_indicator.text = getString(R.string.temperature)
                label_chart.text = getString(R.string.temperature)
                chart.setUpperZoneRed()
            }
            ChartType.Oxygen -> {
                label_indicator.text = getString(R.string.oxygen)
                label_chart.text = getString(R.string.oxygen)
                chart.setUpperZoneGreen()
            }
        }

        action_return.setOnClickListener {
            TankInfoNavigator.navigateToTankInfo(activity as AppCompatActivity)
        }

        action_switch.setOnClickListener { switchChart() }

        selector_period.init(arrayOf(getString(R.string.one_day),
            getString(R.string.three_days),
            getString(R.string.seven_days))) { position, _ ->
            tankInfoViewModel.selectPeriod(position)
        }

        recyclerViewAdapter = recycler_view.init(null, R.layout.layout_item_indicators_table) { it: TableRowDisplay, i: Int ->
            text_time.text = it.time
            text_measurement.text = it.indicator
            text_diff.text = it.diff
        }

        setupChartStyle()

        tankInfoViewModel.selectedTankDisplayLiveData.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                tank_view.text = it.tankNumber
                tank_view.setTankColor(it.tankColor)
            }
        })

        tankInfoViewModel.getTableLiveData(chartType).observe(viewLifecycleOwner, Observer {
            if(it?.rows != null) {
                try {
                    recycler_view.removeItemDecorationAt(0)
                } catch (e: Exception) {
                    // meow
                }
                recycler_view.addItemDecoration(getSectionCallback(it.rows))
            }

            recyclerViewAdapter?.setList(it?.rows)
        })

        tankInfoViewModel.getChartLiveData(chartType).observe(viewLifecycleOwner, Observer {
            setChartData(it)
        })
    }

    private fun setupChartStyle() {
        // Chart
        chart.setTouchEnabled(false)
        chart.description = null
        chart.setNoDataText(getString(R.string.chart_no_data))
        chart.setNoDataTextColor(ContextCompat.getColor(context!!, R.color.colorAccent))
        chart.setDrawGridBackground(true)
        chart.setDrawBorders(false)
        chart.legend.isEnabled = false
        chart.extraBottomOffset = 8.0f
        chart.setDrawGridBackground(false)

        // X axis
        chart.xAxis.textSize = 14.0f
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
//        chart.xAxis.labelRotationAngle = 30.0f
        chart.xAxis.axisMinimum = 0.0f
        chart.xAxis.setAvoidFirstLastClipping(true)

        // Y left axis
        chart.axisLeft.textSize = 14.0f
        chart.axisLeft.granularity = 1.0f
        chart.axisLeft.setDrawLimitLinesBehindData(true)

        // Y right axis
        chart.axisRight.textSize = 14.0f
        chart.axisRight.granularity = 1.0f
    }

    private fun setChartData(data: ChartDisplay?) {
        if(data != null) {
            if(data.chartPointList != null && data.chartXLabels != null) {
                val entries = data.chartPointList.map { chartDisplay ->
                    Entry(chartDisplay.x, chartDisplay.y)
                }

                val lineDataSet = LineDataSet(entries, "My Label")
                lineDataSet.color = ContextCompat.getColor(context!!, R.color.colorAccent)
                lineDataSet.lineWidth = 2.0f
                lineDataSet.setDrawCircles(data.chartPointList.size <= 20)

                val lineData = LineData(lineDataSet)
                lineData.setDrawValues(false)

                val bottomLimitLine = LimitLine(data.indicatorsConstants.bottomLimit)
                bottomLimitLine.lineColor = ContextCompat.getColor(context!!, R.color.colorYellowExtraLight)

                val topLimitLine = LimitLine(data.indicatorsConstants.topLimit)
                topLimitLine.lineColor = ContextCompat.getColor(context!!, R.color.colorYellowExtraLight)

                chart.axisLeft.removeAllLimitLines()
                chart.axisLeft.addLimitLine(bottomLimitLine)
                chart.axisLeft.addLimitLine(topLimitLine)
                chart.axisLeft.axisMinimum = data.indicatorsConstants.minimumValue
                chart.axisLeft.axisMaximum = data.indicatorsConstants.maximumValue
                chart.axisRight.axisMinimum = data.indicatorsConstants.minimumValue
                chart.axisRight.axisMaximum = data.indicatorsConstants.maximumValue
                chart.xAxis.axisMaximum = (data.chartPointList.size - 1).toFloat()
                chart.xAxis.setLabelCount(data.chartXLabels.size, true)
                chart.xAxis.valueFormatter = ChartValueFormatter(data.chartXLabels, data.chartPointList.size)
                chart.data = lineData
                chart.invalidate()
            } else {
                chart.clear()
            }

            text_indicator_time.text = data.indicatorTime
            text_indicator.text = data.indicator
            text_indicator.setIndicatorColor(data.indicatorColor)
        }
    }

    private fun getSectionCallback(list: List<TableRowDisplay>): RecyclerSectionItemDecoration.SectionCallback {
        return object : RecyclerSectionItemDecoration.SectionCallback {
            //In your data, implement a method to determine if this is a section.
            override fun isSection(position: Int): Boolean {
                return position > 0 && position < list.size && (list[position].date != list[position - 1].date)
            }

            //Implement a method that returns a SectionHeader.
            override fun getSectionHeader(position: Int): SectionInfo? =
                SectionInfo(list[position].date)
        }
    }

    private fun switchChart() {
        val iconId = when (view_switcher.nextView.tag) {
            "chart" -> {
                R.drawable.ic_border_all_black_24dp
            }
            else -> {
                R.drawable.ic_show_chart_black_24dp
            }
        }
        action_switch.setImageDrawable(ContextCompat.getDrawable(context!!, iconId))
        view_switcher.showNext()
    }

    companion object {

        @JvmStatic
        fun newInstance(type: ChartType) =
            ChartFragment().apply {
                arguments = Bundle().apply {
                    putString("type", type.name)
                }
            }
    }
}

enum class ChartType {
    Temperature, Oxygen
}

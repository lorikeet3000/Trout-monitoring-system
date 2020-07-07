package ru.arvata.pomor.ui.overview


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_site_overview.*

import ru.arvata.pomor.R
import ru.arvata.pomor.ui.setIndicatorColor

class SiteOverviewFragment : Fragment() {
    private lateinit var viewModel: OverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        viewModel = ViewModelProviders.of(activity!!).get(OverviewViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_site_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.siteLiveData.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                text_tanks_number.text = it.tanksNumber
                text_fish_amount.text = it.fishAmount
                text_all_weight.text = it.allWeight
                text_avg_temperature.text = it.avgTemperatureIndicator
                text_avg_temperature.setIndicatorColor(it.avgTemperatureColor)
                text_avg_temperature_time.text = it.lastTemperatureTime
                text_avg_oxygen.text = it.avgOxygenIndicator
                text_avg_oxygen.setIndicatorColor(it.avgOxygenColor)
                text_avg_oxygen_time.text = it.lastOxygenTime
            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            SiteOverviewFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

package ru.arvata.pomor.ui.mainpage

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_tank_info_indicators.*

import ru.arvata.pomor.R
import ru.arvata.pomor.ui.navigation.TankInfoNavigator
import ru.arvata.pomor.ui.setIndicatorColor

class TankInfoIndicatorsFragment : Fragment() {
    private lateinit var tankInfoViewModel: TankInfoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        tankInfoViewModel = ViewModelProviders.of(activity!!).get(TankInfoViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tank_info_indicators, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        action_temperature_chart.setOnClickListener {
            TankInfoNavigator.navigateToTemperatureChart(activity as AppCompatActivity)
        }

        action_oxygen_chart.setOnClickListener {
            TankInfoNavigator.navigateToOxygenChart(activity as AppCompatActivity)
        }

        tankInfoViewModel.indicatorsLiveData.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                text_temperature_time.text = it.temperatureTime
                text_temperature.text = it.temperatureIndicator
                text_temperature.setIndicatorColor(it.temperatureColor)
                text_oxygen_time.text = it.oxygenTime
                text_oxygen.text = it.oxygenIndicator
                text_oxygen.setIndicatorColor(it.oxygenColor)
            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            TankInfoIndicatorsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

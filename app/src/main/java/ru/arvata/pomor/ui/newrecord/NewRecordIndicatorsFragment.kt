package ru.arvata.pomor.ui.newrecord

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_new_record_indicators.*
import ru.arvata.pomor.R
import ru.arvata.pomor.data.IndicatorType
import ru.arvata.pomor.ui.navigation.NewRecordNavigator
import ru.arvata.pomor.util.DateTimePickerDialog
import ru.arvata.pomor.util.onTextChanged

class NewRecordIndicatorsFragment : Fragment() {
    private lateinit var newRecordViewModel: NewRecordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        newRecordViewModel = ViewModelProviders.of(activity!!).get(NewRecordViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_new_record_indicators, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        action_edit_temperature_time.setOnClickListener {
            NewRecordNavigator.showDateTimePickerDialog(activity as AppCompatActivity, temperatureTimeListener)
        }

        action_edit_oxygen_time.setOnClickListener {
            NewRecordNavigator.showDateTimePickerDialog(activity as AppCompatActivity, oxygenTimeListener)
        }

        input_temperature.onTextChanged {
            newRecordViewModel.setTemperature(it)
        }

        input_oxygen.onTextChanged {
            newRecordViewModel.setOxygen(it)
        }

        newRecordViewModel.recordDisplayLiveData.observe(viewLifecycleOwner, Observer {
            if(it?.temperatureIndicator == false && input_temperature.text?.isNotEmpty() == true) {
                input_temperature.text = null
            }
            if(it?.oxygenIndicator == false && input_oxygen.text?.isNotEmpty() == true) {
                input_oxygen.text = null
            }
            text_temperature_time.text = it?.temperatureTime
            text_oxygen_time.text = it?.oxygenTime
        })

        TroutVoiceRecognizer.voiceLiveData.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                if(it.first == VoiceEvent.Indicator) {
                    val indicatorType = (it.second as Pair<*, *>).first as IndicatorType
                    val indicatorValue = (it.second as Pair<*, *>).second as? Float

                    if(indicatorType == IndicatorType.Temperature) {
                        input_temperature.setText(indicatorValue?.toString())
                    }

                    if(indicatorType == IndicatorType.Oxygen) {
                        input_oxygen.setText(indicatorValue?.toString())
                    }
                }
            }
        })
    }

    private val temperatureTimeListener
            = object : DateTimePickerDialog.OnDateTimeListener {
        override fun onDateTimeSet(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
            newRecordViewModel.setTemperatureTime(year, month, day, hour, minute)
        }
    }

    private val oxygenTimeListener
            = object : DateTimePickerDialog.OnDateTimeListener {
        override fun onDateTimeSet(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
            newRecordViewModel.setOxygenTime(year, month, day, hour, minute)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            NewRecordIndicatorsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

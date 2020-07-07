package ru.arvata.pomor.ui.newrecord

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.fragment_new_record_fish.*
import kotlinx.android.synthetic.main.fragment_new_record_fish.action_edit_time
import ru.arvata.pomor.R
import ru.arvata.pomor.data.IndicatorType
import ru.arvata.pomor.data.Site
import ru.arvata.pomor.ui.navigation.NewRecordNavigator
import ru.arvata.pomor.util.*

class NewRecordFishFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_new_record_fish, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        container_moving_info.visibility = View.GONE

        input_fish_amount.onTextChanged {
            newRecordViewModel.setFishIndicator(it)
        }

        selector_movement_type.init(arrayOf(getString(R.string.mortality), getString(R.string.seeding),  getString(R.string.moving),
            getString(R.string.average_fish_weight), getString(R.string.fish_catch))) { position: Int, text: String? ->
            onMovementTypeSelected(position)
        }

        action_edit_time.setOnClickListener {
            NewRecordNavigator.showDateTimePickerDialog(activity as AppCompatActivity, fishTimeListener)
        }

        val movingReasons = arrayOf(getString(R.string.lower_plant_density),
            getString(R.string.cleaning_tank),
            getString(R.string.fish_illness))
        val movingReasonAdapter = ArrayAdapter(context!!, android.R.layout.simple_dropdown_item_1line,
            movingReasons)
        input_moving_reason.setAdapter(movingReasonAdapter)
        input_moving_reason.onTextChanged {
            newRecordViewModel.setFishMovingReason(it)
        }

        val catchReasons = arrayOf(getString(R.string.vet_examination),
            getString(R.string.measuring_fish_weight), getString(R.string.selling))

        val catchReasonAdapter = ArrayAdapter<String>(context!!, android.R.layout.simple_dropdown_item_1line, catchReasons)
        input_catch_reason.setAdapter(catchReasonAdapter)
        input_catch_reason.onTextChanged {
            newRecordViewModel.setCatchReason(it)
        }

        newRecordViewModel.sitesDisplay.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                selector_moving_destination_site.init(it) { position: Int, text: String? ->
                    newRecordViewModel.setFishMovingDestinationSite(position)
                }
            }
        })

        newRecordViewModel.destinationTanksDisplayLiveData.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                selector_moving_destination_tank.init(it) { i: Int, s: String? ->
                    newRecordViewModel.setFishMovingDestinationTank(i)
                }
            }
        })

        newRecordViewModel.recordDisplayLiveData.observe(viewLifecycleOwner, Observer {
            if(it?.fishIndicator == false && input_fish_amount.text?.isNotEmpty() == true) {
                input_fish_amount.text = null
            }
            if(it?.fishMovingReason == false && input_moving_reason.text?.isNotEmpty() == true) {
                input_moving_reason.text = null
            }
            if(it?.fishCatchReason == false && input_catch_reason.text?.isNotEmpty() == true) {
                input_catch_reason.text = null
            }
            if(it?.fishMovingDestinationSite == false && selector_moving_destination_site.selectedItemPosition != 0) {
                selector_moving_destination_site.setSelection(0)
            }
            if(it?.fishMovingDestinationTank == false && selector_moving_destination_tank.selectedItemPosition != 0) {
                selector_moving_destination_tank.setSelection(0)
            }

            text_fish_time.text = it?.fishIndicatorTime
        })

        TroutVoiceRecognizer.voiceLiveData.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                when {
                    it.first == VoiceEvent.Indicator -> {
                        val indicatorType = (it.second as Pair<*, *>).first as IndicatorType

                        if(indicatorType == IndicatorType.Mortality) {
                            selector_movement_type.setSelection(0)
                            val indicatorValue = (it.second as Pair<*, *>).second as? Int
                            input_fish_amount.setText(indicatorValue?.toString())
                        }
                        if(indicatorType == IndicatorType.Seeding) {
                            selector_movement_type.setSelection(1)
                            val indicatorValue = (it.second as Pair<*, *>).second as? Int
                            input_fish_amount.setText(indicatorValue?.toString())
                        }
                        if(indicatorType == IndicatorType.Moving) {
                            selector_movement_type.setSelection(2)
                            val indicatorValue = (it.second as Pair<*, *>).second as? Int
                            input_fish_amount.setText(indicatorValue?.toString())
                        }
                        if(indicatorType == IndicatorType.Weight) {
                            selector_movement_type.setSelection(3)
                            val indicatorValue = (it.second as Pair<*, *>).second as? Float
                            input_fish_amount.setText(indicatorValue?.toString())
                        }
                        if(indicatorType == IndicatorType.Catch) {
                            selector_movement_type.setSelection(4)
                            val indicatorValue = (it.second as Pair<*, *>).second as? Int
                            input_fish_amount.setText(indicatorValue?.toString())
                        }
                    }
                    it.first == VoiceEvent.MovingDestinationSite -> {
                        selector_movement_type.setSelection(2)
                        selector_moving_destination_site.setSelection(newRecordViewModel.getSitePosition(it.second as? Site))
                    }
                    it.first == VoiceEvent.MovingDestinationTank -> {
                        val position = it.second as? Int
                        selector_moving_destination_tank.setSelection(position ?: 0)
                    }
                    it.first == VoiceEvent.Reason -> {
                        val reasonType = (it.second as Pair<*, *>).first as ReasonType
                        val reasonValue = (it.second as Pair<*, *>).second as String?
                        if(reasonType == ReasonType.Moving) {
                            selector_movement_type.setSelection(2)
                            input_moving_reason.setText(reasonValue)
                        } else {
                            selector_movement_type.setSelection(4)
                            input_catch_reason.setText(reasonValue)
                        }
                    }
                }
            }
        })
    }

    private fun onMovementTypeSelected(position: Int) {
        val type = newRecordViewModel.setFishIndicatorType(position)
        container_moving_info.visibility = if(type == IndicatorType.Moving) {
            View.VISIBLE
        } else {
            View.GONE
        }

        container_catch_reason.visibility = if(type == IndicatorType.Catch) {
            View.VISIBLE
        } else {
            View.GONE
        }

//        input_fish_amount.setText(null)

        if(type == IndicatorType.Weight) {
            label_fish_hint.hint = getString(R.string.fish_weight_kg)
            input_fish_amount.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
        } else {
            label_fish_hint.hint = getString(R.string.amount)
            input_fish_amount.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }

    private val fishTimeListener
            = object : DateTimePickerDialog.OnDateTimeListener {
        override fun onDateTimeSet(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
            newRecordViewModel.setFishTime(year, month, day, hour, minute)
        }
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            NewRecordFishFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

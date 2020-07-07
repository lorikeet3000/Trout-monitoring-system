package ru.arvata.pomor.ui.newrecord


import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_new_record_feeding.*
import kotlinx.android.synthetic.main.fragment_new_record_feeding.action_edit_time
import ru.arvata.pomor.R
import ru.arvata.pomor.data.IndicatorType
import ru.arvata.pomor.ui.navigation.NewRecordNavigator
import ru.arvata.pomor.util.*
import java.util.*

class NewRecordFeedingFragment : Fragment() {
    private lateinit var newRecordViewModel: NewRecordViewModel
    private lateinit var fromDatePickerDialog: DatePickerDialog
    private lateinit var toDatePickerDialog: DatePickerDialog

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
        return inflater.inflate(R.layout.fragment_new_record_feeding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        input_feeding_amount.onTextChanged {
            newRecordViewModel.setFeeding(it)
        }

        action_edit_time.setOnClickListener {
            NewRecordNavigator.showDateTimePickerDialog(activity as AppCompatActivity, feedingTimeListener)
        }

        newRecordViewModel.feedProducerLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                selector_producer.init(it) { position: Int, text: String? ->
                    newRecordViewModel.setFeedProducer(position)
                }
            }
        })

        input_coef.onTextChanged {
            newRecordViewModel.setFeedCoef(it)
        }

        val calendar = Calendar.getInstance()
        fromDatePickerDialog = DatePickerDialog(context!!, feedPeriodFromListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))

        toDatePickerDialog = DatePickerDialog(context!!, feedPeriodToListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH))

        date_picker_from.setOnClickListener {
            fromDatePickerDialog.show()
        }

        date_picker_to.setOnClickListener {
            toDatePickerDialog.show()
        }

        newRecordViewModel.recordDisplayLiveData.observe(viewLifecycleOwner, Observer {
            if (it?.feedingIndicator == false && input_feeding_amount.text?.isNotEmpty() == true) {
                input_feeding_amount.text = null
            }

            text_feeding_time.text = it?.feedingTime

            if(it?.feedProducer == false && selector_producer.selectedItemPosition != 0) {
                selector_producer.setSelection(0)
            }
            if(it?.feedCoef == false && input_coef.text?.isNotEmpty() == true) {
                input_coef.text = null
            }
            if(it?.feedPeriodFrom == false && date_picker_from.text?.isNotEmpty() == true) {
                date_picker_from.text = null
            }
            if(it?.feedPeriodTo == false && date_picker_to.text?.isNotEmpty() == true) {
                date_picker_to.text = null
            }
        })

        TroutVoiceRecognizer.voiceLiveData.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                if(it.first == VoiceEvent.Indicator) {
                    val indicatorType = (it.second as Pair<*, *>).first as IndicatorType

                    if (indicatorType == IndicatorType.Feeding) {
                        val indicatorValue = (it.second as Pair<*, *>).second as? Float
                        input_feeding_amount.setText(indicatorValue?.toString())
                    }
                } else if(it.first == VoiceEvent.FeedProducer) {
                    val feedProducerType = it.second as? FeedProducerType
                    val position = if(feedProducerType != null) {
                        newRecordViewModel.getFeedProducerPosition(feedProducerType)
                    } else 0

                    selector_producer.setSelection(position)
                } else if(it.first == VoiceEvent.FeedCoef) {
                    input_coef.setText(it.second?.toString())
                } else if(it.first == VoiceEvent.FeedPeriodFrom) {
                    setFeedPeriodFromDate(it.second as? ddMMyyyy)
                } else if(it.first == VoiceEvent.FeedPeriodTo) {
                    setFeedPeriodToDate(it.second as? ddMMyyyy)
                }
            }
        })
    }

    private val feedPeriodFromListener = DatePickerDialog.OnDateSetListener { view, year, month, day ->
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val dateText = getDdMMyyyyString(calendar.time)

        setFeedPeriodFromDate(dateText)
    }

    private val feedPeriodToListener = DatePickerDialog.OnDateSetListener { view, year, month, day ->
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, day)
        val dateText = getDdMMyyyyString(calendar.time)

        setFeedPeriodToDate(dateText)
    }

    private val feedingTimeListener = object : DateTimePickerDialog.OnDateTimeListener {
        override fun onDateTimeSet(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
            newRecordViewModel.setFeedingTime(year, month, day, hour, minute)
        }
    }

    private fun setFeedPeriodFromDate(date: ddMMyyyy?) {
        date_picker_from.text = date
        newRecordViewModel.setFeedPeriodFrom(date)
    }

    private fun setFeedPeriodToDate(date: ddMMyyyy?) {
        date_picker_to.text = date
        newRecordViewModel.setFeedPeriodTo(date)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            NewRecordFeedingFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

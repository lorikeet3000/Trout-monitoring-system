package ru.arvata.pomor.ui.newrecord

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_new_record.*
import ru.arvata.pomor.R
import ru.arvata.pomor.data.IndicatorType
import ru.arvata.pomor.data.Site
import ru.arvata.pomor.ui.navigation.NewRecordNavigator
import ru.arvata.pomor.util.*
import java.lang.Exception

class NewRecordFragment : Fragment(), TabLayout.OnTabSelectedListener {
    private lateinit var newRecordViewModel: NewRecordViewModel
    private var tabBadger: TabBadger? = null

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
        return inflater.inflate(R.layout.fragment_new_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tab_layout.addOnTabSelectedListener(this)

        if(savedInstanceState == null) {
            tab_layout.getTabAt(0)?.select()
        }

        action_voice_command.setOnClickListener {
            try {
                TroutVoiceRecognizer.onKeywordTriggered()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, R.string.error_init_voice_input, Toast.LENGTH_SHORT).show()
            }
        }

        action_save_record.setOnClickListener {
            validateAndConfirmRecord()
        }

        tabBadger = TabBadger(tab_layout, getString(R.string.indicators), getString(R.string.feeding),
            getString(R.string.fish))

        newRecordViewModel.sitesDisplay.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                selector_site.init(it) { i: Int, s: String? ->
                    newRecordViewModel.setSite(i)
                }
            }
        })

        newRecordViewModel.siteTanksDisplayLiveData.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                selector_tank.init(it) { position: Int, text: String? ->
                    newRecordViewModel.setTank(position)
                }
            }
        })

        newRecordViewModel.tabsChangedLiveData.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                tabBadger?.setTabChanged(it)
            }
        })

        TroutVoiceRecognizer.setValidator(newRecordViewModel.validator)
        TroutVoiceRecognizer.voiceLiveData.observe(viewLifecycleOwner, Observer {
            if(it != null) {
//                loge("${it.first}, ${it.second}")
                when(it.first) {
                    VoiceEvent.ConfirmRecord -> newRecordViewModel.confirmRecord()
                    VoiceEvent.FinishWork -> cancelRecord()
                    VoiceEvent.SiteName -> selector_site.setSelection(newRecordViewModel.getSitePosition(it.second as? Site))
                    VoiceEvent.TankNumber -> selector_tank.setSelection((it.second as? Int) ?: 0)
                    VoiceEvent.Indicator -> {
                        val indicatorType = (it.second as Pair<*, *>).first as IndicatorType
                        when (indicatorType) {
                            IndicatorType.Temperature, IndicatorType.Oxygen -> showTab(0)
                            IndicatorType.Feeding -> showTab(1)
                            else -> showTab(2)
                        }
                    }
                    VoiceEvent.FeedProducer, VoiceEvent.FeedCoef,
                    VoiceEvent.FeedPeriodFrom, VoiceEvent.FeedPeriodTo -> {
                        showTab(1)
                    }
                }
            }
        })
    }

    private fun validateAndConfirmRecord() {
        val validationResult = newRecordViewModel.validateRecord()

        if(validationResult == ValidationResult.Success) {
            newRecordViewModel.confirmRecord()
        }

        val text = when(validationResult) {
            ValidationResult.ErrorNoSite -> getString(R.string.error_no_site)
            ValidationResult.ErrorNoTank -> getString(R.string.error_no_tank)
            ValidationResult.ErrorNoIndicators -> getString(R.string.error_no_indicators)
            ValidationResult.ErrorNoFeedProducer -> getString(R.string.error_no_feed_producer)
            ValidationResult.ErrorNoFeedCoef -> getString(R.string.error_no_feed_coef)
            ValidationResult.ErrorNoFeedPeriodFrom -> getString(R.string.error_no_feed_period_from)
            ValidationResult.ErrorNoFeedPeriodTo -> getString(R.string.error_no_feed_period_to)
            ValidationResult.ErrorNoMovingDestinationSite -> getString(R.string.error_no_moving_destination_site)
            ValidationResult.ErrorNoMovingDestinationTank -> getString(R.string.error_no_moving_destination_tank)
            ValidationResult.Success -> getString(R.string.succes_indicators_added)
            ValidationResult.ErrorNoRecord -> getString(R.string.error_no_record)
        }
        Toast.makeText(context!!, text, Toast.LENGTH_SHORT).show()

        when(validationResult) {
            ValidationResult.ErrorNoFeedProducer, ValidationResult.ErrorNoFeedCoef,
            ValidationResult.ErrorNoFeedPeriodFrom, ValidationResult.ErrorNoFeedPeriodTo -> showTab(1)
            ValidationResult.ErrorNoMovingDestinationSite, ValidationResult.ErrorNoMovingDestinationTank -> showTab(2)
        }
    }

    private fun cancelRecord() {
        newRecordViewModel.cancelRecord()
        showTab(0)
        Toast.makeText(context!!, getString(R.string.record_deleted), Toast.LENGTH_SHORT).show()
    }

    private fun showTab(index: Int) {
        when (index) {
            0 -> NewRecordNavigator.showIndicatorsTab(childFragmentManager)
            1 -> NewRecordNavigator.showFeedingTab(childFragmentManager)
            2 -> NewRecordNavigator.showFishTab(childFragmentManager)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        if(tab != null) {
            showTab(tab.position)
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {}

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if(tab != null) {
            showTab(tab.position)
        }
    }

    private fun onKeyboardShow() {
        action_save_record.visibility = View.GONE
    }

    private fun onKeyboardHide() {
        action_save_record.visibility = View.VISIBLE
    }

    private val keyboardBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val isKeyboardOpen = intent?.extras?.getBoolean(BROADCAST_KEYBOARD_IS_OPEN_EXTRA) ?: false
            if(isKeyboardOpen) {
                onKeyboardShow()
            } else {
                onKeyboardHide()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(App.appContext).registerReceiver(keyboardBroadcastReceiver,
            IntentFilter(BROADCAST_KEYBOARD))
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(App.appContext).unregisterReceiver(keyboardBroadcastReceiver)
        super.onStop()
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            NewRecordFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

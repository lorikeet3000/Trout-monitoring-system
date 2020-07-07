package ru.arvata.pomor.ui.mainpage

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_tank_info.*
import kotlinx.android.synthetic.main.layout_tank_info.*
import ru.arvata.pomor.R
import ru.arvata.pomor.ui.TankColor
import ru.arvata.pomor.ui.navigation.TankInfoNavigator

class TankInfoFragment : Fragment(), TabLayout.OnTabSelectedListener {
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
        return inflater.inflate(R.layout.fragment_tank_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        action_prev_tank.setOnClickListener { tankInfoViewModel.selectPrevTank() }
        action_next_tank.setOnClickListener { tankInfoViewModel.selectNextTank() }

        tab_layout.addOnTabSelectedListener(this)

        tankInfoViewModel.tankInfoLiveData.observeForever(tankObserver)

        if(savedInstanceState == null) {
            tab_layout.getTabAt(0)?.select()
        }
    }

    private val tankObserver = Observer {tankInfo: TankInfoDisplay? ->
        if(tankInfo == null) return@Observer

        text_tank_title.text = tankInfo.tankNumber
        text_fish_weight.text = tankInfo.weight
        text_fish_amount.text = tankInfo.amount
        text_all_weight.text = tankInfo.allWeight

        val backgroundColorId = when (tankInfo.tankColor) {
            TankColor.Red -> R.color.colorRed
            TankColor.Yellow -> R.color.colorYellow
            TankColor.Green -> R.color.colorGreen
            else -> R.color.colorGrey
        }

        val textColorId = when (tankInfo.tankColor) {
            TankColor.Red -> R.color.textColorOnRedLight
            TankColor.Yellow -> R.color.textColorOnYellowLight
            TankColor.Green -> R.color.textColorOnGreenLight
            else -> R.color.textColorOnGreyLight
        }

        tank_background.backgroundTintList = ContextCompat.getColorStateList(context!!, backgroundColorId)
        text_tank_title.setTextColor(ContextCompat.getColor(context!!, textColorId))
        text_fish_weight.setTextColor(ContextCompat.getColor(context!!, textColorId))
        text_fish_amount.setTextColor(ContextCompat.getColor(context!!, textColorId))
        text_all_weight.setTextColor(ContextCompat.getColor(context!!, textColorId))
    }

    private fun showTab(position: Int) {
        when (position) {
            0 -> {
                TankInfoNavigator.showIndicatorsTab(childFragmentManager)
            }
            1 -> {
                TankInfoNavigator.showFeedingTab(childFragmentManager)
            }
            2 -> {
                TankInfoNavigator.showFishTab(childFragmentManager)
            }
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        if(tab != null) {
            showTab(tab.position)
        }
    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {}

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if(tab != null) {
            showTab(tab.position)
        }
    }

    override fun onDestroyView() {
        tankInfoViewModel.tankInfoLiveData.removeObserver(tankObserver)
        super.onDestroyView()
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            TankInfoFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

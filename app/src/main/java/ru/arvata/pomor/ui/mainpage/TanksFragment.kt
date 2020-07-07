package ru.arvata.pomor.ui.mainpage


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_tanks.*
import kotlinx.android.synthetic.main.layout_bottom_sheet.*
import ru.arvata.pomor.R
import ru.arvata.pomor.ui.TankColor
import ru.arvata.pomor.ui.navigation.MainNavigator
import ru.arvata.pomor.ui.navigation.TankInfoNavigator
import ru.arvata.pomor.ui.setTankColor
import ru.arvata.pomor.util.LockableBottomSheetBehavior
import ru.arvata.pomor.util.TankView

class TanksFragment : Fragment() {
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var tankInfoViewModel: TankInfoViewModel
    private val tankViews = mutableMapOf<String, TankView>() // tankNumber -> tankView

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
        return inflater.inflate(R.layout.fragment_tanks_pomor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetBehavior = BottomSheetBehavior.from(layout_bottom_sheet)
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback)

        action_toggle.setOnClickListener {
            if(tankInfoViewModel.isTankSelected()) {
                toggleBottomSheet(bottomSheetBehavior)
            } else {
                Toast.makeText(context, getString(R.string.select_tank), Toast.LENGTH_SHORT).show()
            }
        }

        val tankContainer = tanks_container as? ViewGroup
        initTankViews(tankContainer)

        if(savedInstanceState == null) {
            TankInfoNavigator.navigateToTankInfo(activity as AppCompatActivity)
        }

        action_new_record.setOnClickListener {
            MainNavigator.navigateToNewRecord(activity as AppCompatActivity) // todo not like this
        }

        tankInfoViewModel.tanksDisplayLiveData.observe(viewLifecycleOwner, Observer { tankList: List<TankDisplay>? ->
            setTankViewsColor(tankList)
        })
    }

    private fun initTankViews(tankContainer: ViewGroup?) {
        tankContainer?.let { container: ViewGroup ->
            for(i in 0..container.childCount) {
                val tankView = container.getChildAt(i) as? TankView
                tankView?.let {
                    tankViews[tankView.tag as String] = tankView
                    tankView.setOnClickListener { tank ->
                        val tankNumber = (tank.tag as String).toInt()
                        selectTank(tankNumber)
                    }
                }
            }
        }
    }

    private fun setTankViewsColor(tankList: List<TankDisplay>?) {
        for (tankView in tankViews.values) {
            tankView.setTankColor(TankColor.Unknown)
        }

        if(tankList == null || tankList.isEmpty()) {
            return
        }

        for(tank in tankList) {
            val tankView = tankViews[tank.tankNumber]
            tankView?.setTankColor(tank.tankColor)
        }
    }

    private fun selectTank(tankNumber: Int) {
        if(tankInfoViewModel.selectTank(tankNumber)) {
            showBottomSheet(bottomSheetBehavior)
        } else {
            Toast.makeText(context, getString(R.string.error_no_such_tank), Toast.LENGTH_SHORT).show()
        }
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {}

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if(newState == BottomSheetBehavior.STATE_DRAGGING || newState == BottomSheetBehavior.STATE_SETTLING) {
                return
            }

            if(newState == BottomSheetBehavior.STATE_EXPANDED) {
                (bottomSheetBehavior as? LockableBottomSheetBehavior)?.setLocked(true)
            } else {
                (bottomSheetBehavior as? LockableBottomSheetBehavior)?.setLocked(false)
            }

            val drawableId = when (newState) {
                BottomSheetBehavior.STATE_EXPANDED -> R.drawable.ic_expand_more_black_24dp
                else -> R.drawable.ic_expand_less_black_24dp
            }
            action_toggle.setImageDrawable(ContextCompat.getDrawable(this@TanksFragment.context!!, drawableId))
        }
    }

    private fun toggleBottomSheet(bottomSheet: BottomSheetBehavior<View>) {
        if(bottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private fun showBottomSheet(bottomSheet: BottomSheetBehavior<View>) {
        bottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            TanksFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

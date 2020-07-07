package ru.arvata.pomor.ui.mainpage

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_tank_info_fish.*

import ru.arvata.pomor.R
import ru.arvata.pomor.ui.setIndicatorColor

class TankInfoFishFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_tank_info_fish, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tankInfoViewModel.fishLiveData.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                text_mortality_time.text = it.mortalityTime
                text_mortality_amount.text = it.mortalityIndicator
                text_mortality_amount.setIndicatorColor(it.mortalityColor)

                text_seeding_time.text = it.seedingTime
                text_seeding_amount.text = it.seedingIndicator
                text_seeding_amount.setIndicatorColor(it.seedingColor)

                text_moving_time.text = it.movingTime
                text_moving_amount.text = it.movingIndicator
                text_moving_amount.setIndicatorColor(it.movingColor)
                text_moving_destination.text = it.movingDestination

                text_catch_time.text = it.catchTime
                text_catch_amount.text = it.catchIndicator
                text_catch_amount.setIndicatorColor(it.catchColor)
            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            TankInfoFishFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

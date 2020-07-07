package ru.arvata.pomor.ui.mainpage


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_tank_info_feeding.*

import ru.arvata.pomor.R
import ru.arvata.pomor.ui.setIndicatorColor

class TankInfoFeedingFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_tank_info_feeding, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tankInfoViewModel.feedingLiveData.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                text_time.text = it.feedingTime
                text_amount.text = it.feedingIndicator
                text_amount.setIndicatorColor(it.feedingColor)
                text_producer.text = it.feedProducer
            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            TankInfoFeedingFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

package ru.arvata.pomor.ui.overview

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_area_overview.*

import ru.arvata.pomor.R

class AreaOverviewFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_area_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.areaLiveData.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                text_sites_number.text = it.sitesNumber
                text_tanks_number.text = it.tanksNumber
                text_fish_amount.text = it.fishAmount
                text_all_weight.text = it.allWeight
            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            AreaOverviewFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

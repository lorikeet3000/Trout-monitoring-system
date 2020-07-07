package ru.arvata.pomor.ui.overview

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.arvata.pomor.R
import ru.arvata.pomor.ui.navigation.OverviewNavigator

class OverviewFragment : Fragment() {
    private lateinit var overviewViewModel: OverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        overviewViewModel = ViewModelProviders.of(activity!!).get(OverviewViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_overview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        overviewViewModel.selectedSiteLiveData.observe(viewLifecycleOwner, Observer {site ->
            if(site == null) {
                OverviewNavigator.showAreaOverview(childFragmentManager)
            } else {
                OverviewNavigator.showSiteOverview(childFragmentManager)
            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            OverviewFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

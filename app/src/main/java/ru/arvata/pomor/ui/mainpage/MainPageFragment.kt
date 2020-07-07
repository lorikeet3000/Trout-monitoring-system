package ru.arvata.pomor.ui.mainpage


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import ru.arvata.pomor.R
import ru.arvata.pomor.ui.navigation.MainPageNavigator

class MainPageFragment : Fragment() {
    private lateinit var viewModel: MainPageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        viewModel = ViewModelProviders.of(activity!!).get(MainPageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.sitesLiveData.observe(viewLifecycleOwner, Observer {
            if(it == null) {
                MainPageNavigator.showSitesScreen(childFragmentManager)
            } else {
                MainPageNavigator.showTanksScreen(childFragmentManager)
            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            MainPageFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

package ru.arvata.pomor.ui.mainpage

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_sites.*
import kotlinx.android.synthetic.main.layout_item_sites.view.*

import ru.arvata.pomor.R
import ru.arvata.pomor.ui.main.MainViewModel
import ru.arvata.pomor.ui.setTankColor
import ru.arvata.pomor.util.init

class SitesFragment : Fragment() {
    private lateinit var viewModel: SitesViewModel
    private lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
        viewModel = ViewModelProviders.of(activity!!).get(SitesViewModel::class.java)
        mainViewModel = ViewModelProviders.of(activity!!).get(MainViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.sitesLiveData.observe(viewLifecycleOwner, Observer {list ->
            recycler_view.init(list, R.layout.layout_item_sites) { siteDisplay: SiteDisplay, position: Int ->
                this.site_name.text = siteDisplay.name
                this.site_name.setTankColor(siteDisplay.color)
                this.tanks_number.text = resources.getQuantityString(R.plurals.tanks_number,
                    siteDisplay.tanksNumber,
                    siteDisplay.tanksNumber)
                this.tanks_number_green.text = siteDisplay.greenNumber
                this.tanks_number_yellow.text = siteDisplay.yellowNumber
                this.tanks_number_red.text = siteDisplay.redNumber
                this.container_site.setOnClickListener {
                    mainViewModel.setSite(position + 1)
                }
            }
        })
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            SitesFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

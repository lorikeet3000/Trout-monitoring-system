package ru.arvata.pomor.ui.plans

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.arvata.pomor.R
import ru.arvata.pomor.ui.navigation.PlansNavigator

class PlansFragment : Fragment() {
    private lateinit var viewModel: PlansViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        viewModel = ViewModelProviders.of(activity!!).get(PlansViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plans, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        PlansNavigator.navigateToPlansList(activity as AppCompatActivity)
    }

    companion object {

        @JvmStatic
        fun newInstance() =
            PlansFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
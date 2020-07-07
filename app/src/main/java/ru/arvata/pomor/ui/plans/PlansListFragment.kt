package ru.arvata.pomor.ui.plans

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_plans_list.*
import kotlinx.android.synthetic.main.layout_item_plans.view.*

import ru.arvata.pomor.R
import ru.arvata.pomor.ui.navigation.PlansNavigator
import ru.arvata.pomor.ui.setOverdue
import ru.arvata.pomor.ui.setPlanStatus
import ru.arvata.pomor.util.RecyclerViewAdapter
import ru.arvata.pomor.util.init

class PlansListFragment : Fragment() {
    private lateinit var viewModel: PlansViewModel
    private lateinit var createPlanViewModel: CreatePlanViewModel
    private var recyclerViewAdapter: RecyclerViewAdapter<PlanDisplay>? = null
    private lateinit var plansFilterView: PlansFilterView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        viewModel = ViewModelProviders.of(activity!!).get(PlansViewModel::class.java)
        createPlanViewModel = ViewModelProviders.of(activity!!).get(CreatePlanViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_plans_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        plansFilterView = PlansFilterView(container_filter, viewLifecycleOwner, viewModel)

        plansFilterView.close()

        action_add_plan.setOnClickListener {
            PlansNavigator.showCreatePlan(activity as AppCompatActivity, null)
        }

        action_filter.setOnClickListener { plansFilterView.show() }

        recyclerViewAdapter = recycler_view.init(null, R.layout.layout_item_plans) {it: PlanDisplay, i: Int ->
            this.apply {
                title.text = it.title
                tanks.text = it.tanks
                status.setPlanStatus(it.status)
                completeTime.text = it.completeTime
                completeTime.setOverdue(it.overdue)
                comment.visibility = View.GONE
                executors.text = it.executors
                createdBy.text = it.createdBy
                description.text = it.description
                description.visibility = View.GONE
                action_select_item.setOnClickListener { view ->
                    PlansNavigator.showPlanInfo(activity as AppCompatActivity, it.id)
                }
            }
        }

        viewModel.plansDisplayLiveData.observe(viewLifecycleOwner, Observer {
            recyclerViewAdapter?.setList(it)
            recycler_view.scrollToPosition(0)
        })

        viewModel.filterTextLiveData.observe(viewLifecycleOwner, Observer {
            text_filters.text = it
            text_filters.visibility = if(it != null && it.isNotEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })
    }
    companion object {

        @JvmStatic
        fun newInstance() =
            PlansListFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}

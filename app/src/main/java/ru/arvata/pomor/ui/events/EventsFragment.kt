package ru.arvata.pomor.ui.events

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_events.*
import kotlinx.android.synthetic.main.layout_item_events.view.*
import ru.arvata.pomor.R
import ru.arvata.pomor.util.RecyclerViewAdapter
import ru.arvata.pomor.util.init

class EventsFragment : Fragment() {
    private lateinit var viewModel: EventsViewModel
    private var recyclerViewAdapter: RecyclerViewAdapter<EventDisplay>? = null
    private lateinit var filterView: EventsFilterView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        viewModel = ViewModelProviders.of(activity!!).get(EventsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        filterView = EventsFilterView(container_filter, viewLifecycleOwner, viewModel)
        filterView.close()

        action_filter.setOnClickListener {filterView.show() }

        recyclerViewAdapter = recycler_view.init(null, R.layout.layout_item_events, reverse = true) { it: EventDisplay, i: Int ->
            this.text_time.text = it.time
            this.text_tank.text = it.tank
            this.text_user.text = it.user
            this.text_event.text = it.description
        }

        viewModel.eventsDisplay.observe(viewLifecycleOwner, Observer { list: List<EventDisplay>? ->
            recyclerViewAdapter?.setList(list)
        })

        viewModel.filterTextDisplay.observe(viewLifecycleOwner, Observer {
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
            EventsFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}
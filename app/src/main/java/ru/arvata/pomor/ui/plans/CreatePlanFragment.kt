package ru.arvata.pomor.ui.plans

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.chip.Chip
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_create_plan.*

import ru.arvata.pomor.R
import ru.arvata.pomor.data.RemoteRepository
import ru.arvata.pomor.ui.navigation.PlansNavigator
import ru.arvata.pomor.util.DateTimePickerDialog

class CreatePlanFragment : Fragment() {
    private val PLAN_ID = "pland_id"
    private var planId: String? = null
    private lateinit var viewModel: CreatePlanViewModel
    private lateinit var executorsDialog: AlertDialog
    private lateinit var tanksDialog: AlertDialog
    private var users: Array<String>? = null
    private var allTanks: Array<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            planId = it.getString(PLAN_ID)
        }

        viewModel = ViewModelProviders.of(activity!!).get(CreatePlanViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_plan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        action_add_executor.setOnClickListener { showExecutorsDialog() }

        action_add_tank.setOnClickListener { showTanksDialog() }

        action_confirm_plan.setOnClickListener { confirmPlan() }

        action_cancel_plan.setOnClickListener {
            //todo cancel current plan
            PlansNavigator.closeCreatePlan(activity as AppCompatActivity)
        }

        date_picker_from.setOnClickListener {
            showDueFromDateTimePickerDialog(childFragmentManager, dueFromTimeListener)
        }

        date_picker_dueTo.setOnClickListener {
            showDueToDateTimePickerDialog(childFragmentManager, dueToTimeListener)
        }

        viewModel.createPlanStatus.observe(viewLifecycleOwner, Observer {
            when(it) {
                RemoteRepository.NetworkStatus.Progress -> {
                    action_confirm_plan.isEnabled = false
                }
                RemoteRepository.NetworkStatus.Success -> {
                    action_confirm_plan.isEnabled = true
                    PlansNavigator.closeCreatePlan(activity as AppCompatActivity)
                }
                RemoteRepository.NetworkStatus.Error -> {
                    action_confirm_plan.isEnabled = true
                }
            }
        })

        viewModel.usersDisplay.observe(viewLifecycleOwner, Observer { users = it }) // к сожалению, приходится везде подписываться на это

        viewModel.allTanksDisplay.observe(viewLifecycleOwner, Observer {  allTanks = it })

        viewModel.executorsDisplay.observe(viewLifecycleOwner, Observer {
            updateExecutorsChips(it)
        })

        viewModel.tanksDisplay.observe(viewLifecycleOwner, Observer {
            updateTanksChips(it)
        })

        viewModel.dueFromDisplay.observe(viewLifecycleOwner, Observer {
            date_picker_from.text = it
        })

        viewModel.dueToDisplay.observe(viewLifecycleOwner, Observer {
            date_picker_dueTo.text = it
        })

        viewModel.titleLiveData.observe(viewLifecycleOwner, Observer {
            if(input_title.text?.toString() != it) {
                input_title.setText(it)
            }
        })

        viewModel.descriptionLiveData.observe(viewLifecycleOwner, Observer {
            if(input_description.text?.toString() != it) {
                input_description.setText(it)
            }
        })

        planId?.let {
            viewModel.loadPlan(it)
        }
    }


    private val dueFromTimeListener = object : DateTimePickerDialog.OnDateTimeListener {
        override fun onDateTimeSet(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
            viewModel.setDueFromTime(year, month, day, hour, minute)
        }
    }

    private val dueToTimeListener = object : DateTimePickerDialog.OnDateTimeListener {
        override fun onDateTimeSet(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
            viewModel.setDueToTime(year, month, day, hour, minute)
        }
    }

    private fun confirmPlan() {
        val title = input_title.text?.toString()
        val description = input_description.text?.toString()

        clearValidationErrors()

        val validationResult = viewModel.confirmPlan(planId, title, description)

        if(validationResult != null) {
            showValidationError(validationResult)
        }
    }

    private fun showValidationError(validationError: ValidationError) {
        when (validationError) {
            ValidationError.NoTitle -> layout_input_title.setError("Введите заголовок")
            ValidationError.NoDueFrom -> date_picker_from.setError("Добавьте дату")
            ValidationError.NoDueTo -> date_picker_dueTo.setError("Добавьте дату")
            ValidationError.NoExecutors -> label_executors.setError("Добавьте исполнителей")
            ValidationError.NoTanks -> label_tanks.setError("Добавьте садки")
        }
    }

    private fun clearValidationErrors() {
        layout_input_title.setError(null)
        date_picker_from.setError(null)
        date_picker_dueTo.setError(null)
        label_executors.setError(null)
        label_tanks.setError(null)
    }

    private fun showExecutorsDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder
            .setTitle("Выберите исполнителя")
            .setItems(users) { dialog, which ->
                // The 'which' argument contains the index position
                // of the selected item
                viewModel.addExecutor(which)
            }
        executorsDialog = builder.create()
        executorsDialog.show()
    }

    private fun showTanksDialog() {
        val builder = AlertDialog.Builder(context!!)
        builder
            .setTitle("Выберите садок")
            .setItems(allTanks) { dialog, which ->
                // The 'which' argument contains the index position
                // of the selected item
                viewModel.addTank(which)
            }
        tanksDialog = builder.create()
        tanksDialog.show()
    }

    private fun showDueFromDateTimePickerDialog(fragmentManager: FragmentManager, listener: DateTimePickerDialog.OnDateTimeListener) {
        val tag = "dueFrom_date_time_picker"
        val dialog = fragmentManager.findFragmentByTag(tag) as? DateTimePickerDialog
            ?: DateTimePickerDialog()
        dialog.setOnDateTimeListener(listener)
        dialog.show(fragmentManager, tag)
    }

    private fun showDueToDateTimePickerDialog(fragmentManager: FragmentManager, listener: DateTimePickerDialog.OnDateTimeListener) {
        val tag = "dueTo_date_time_picker"
        val dialog = fragmentManager.findFragmentByTag(tag) as? DateTimePickerDialog
            ?: DateTimePickerDialog()
        dialog.setOnDateTimeListener(listener)
        dialog.show(fragmentManager, tag)
    }

    private fun updateExecutorsChips(list: List<Pair<String, String>>?) {
        chips_executors.removeAllViews()
        if(list != null) {
            for (executor in list) {
                val chip = Chip(context!!)
                chip.text = executor.first
                chip.tag = executor.second
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {view ->
                    viewModel.removeExecutor(view.tag as String)
                }
                chips_executors.addView(chip)
            }
        }
    }

    private fun updateTanksChips(list: List<Pair<String, String>>?) {
        chips_tanks.removeAllViews()
        if(list != null) {
            for (tank in list) {
                val chip = Chip(context!!)
                chip.text = tank.first
                chip.tag = tank.second
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {view ->
                    viewModel.removeTank(view.tag as String)
                }
                chips_tanks.addView(chip)
            }
        }
    }

    companion object {

        @JvmStatic
        fun newInstance(planId: String?) =
            CreatePlanFragment().apply {
                arguments = Bundle().apply {
                    putString(PLAN_ID, planId)
                }
            }
    }
}

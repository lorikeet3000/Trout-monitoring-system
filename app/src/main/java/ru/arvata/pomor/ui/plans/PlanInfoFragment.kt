package ru.arvata.pomor.ui.plans

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_plan_info.*
import kotlinx.android.synthetic.main.layout_comment_dialog.view.*
import kotlinx.android.synthetic.main.layout_item_plans.*
import ru.arvata.pomor.R
import ru.arvata.pomor.data.PlanStatus
import ru.arvata.pomor.data.RemoteRepository
import ru.arvata.pomor.ui.navigation.PlansNavigator
import ru.arvata.pomor.ui.setOverdue
import ru.arvata.pomor.ui.setPlanStatus
import ru.arvata.pomor.util.loge

class PlanInfoFragment : Fragment() {
    private val PLAN_ID = "plan_id"
    private lateinit var plansViewModel: PlansViewModel
    private lateinit var planId: String
    private lateinit var deletePlanDialog: AlertDialog
    private lateinit var networkDialog: AlertDialog
    private lateinit var refusePlanDialog: AlertDialog
    private var completeDialog: AlertDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val id = arguments?.getString(PLAN_ID)
        if(id == null) {
            PlansNavigator.closePlanInfo(activity as AppCompatActivity)
            return
        } else {
            planId = id
        }

        plansViewModel = ViewModelProviders.of(activity!!).get(PlansViewModel::class.java)
        plansViewModel.selectPlan(planId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_plan_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        networkDialog = createNetworkDialog()
        deletePlanDialog = createDeletePlanDialog()
        refusePlanDialog = createRefusePlanDialog()

        plans_toolbar.title = "Вернуться к списку"
        plans_toolbar.setNavigationOnClickListener {
            PlansNavigator.closePlanInfo(activity as AppCompatActivity)
        }

        plansViewModel.planInfoLiveData.observe(viewLifecycleOwner, Observer {
            if(it != null) {
                title.text = it.title
                tanks.text = it.tanks
                status.setPlanStatus(it.status)
                completeTime.text = it.completeTime
                completeTime.setOverdue(it.overdue)
                comment.text = it.comment
                comment.visibility = if(it.comment.isNullOrEmpty()) View.GONE else View.VISIBLE
                executors.text = it.executors
                createdBy.text = it.createdBy
                description.text = if(it.description.isNullOrEmpty()) "Без описания" else it.description
                checkbox_plan_status.isChecked = it.status == PlanStatus.Completed

                if(it.canEdit) {
                    container_edit_plan.visibility = View.VISIBLE

                    action_delete_plan.setOnClickListener {
                        deletePlanDialog.show()
                    }

                    action_edit_plan.setOnClickListener { view ->
                        PlansNavigator.showCreatePlan(activity as AppCompatActivity, it.id)
                    }
                } else {
                    container_edit_plan.visibility = View.GONE
                }

                if(it.canComplete) {
                    container_plan_status.visibility = View.VISIBLE
                    action_refuse_plan.setOnClickListener {
                        refusePlanDialog.show()
                    }
                    checkbox_plan_status.setOnClickListener { buttonView ->
                        completeDialog = createCompleteDialog(checkbox_plan_status.isChecked)
                        completeDialog?.show()
                    }
                } else {
                    container_plan_status.visibility = View.GONE
                }
            }
        })

        plansViewModel.deletePlanStatus.observe(viewLifecycleOwner, Observer {
            loge("delete ${it?.name}")
            showNetworkDialog(it)

            if(it == RemoteRepository.NetworkStatus.Success) {
                PlansNavigator.closePlanInfo(activity as AppCompatActivity)
                return@Observer
            }
        })

        plansViewModel.changePlanStatus.observe(viewLifecycleOwner, Observer {
            showNetworkDialog(it)
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as? AppCompatActivity)?.supportActionBar?.hide()
    }

    override fun onPause() {
        (activity as? AppCompatActivity)?.supportActionBar?.show()
        super.onPause()
    }

    private fun showNetworkDialog(status: RemoteRepository.NetworkStatus?) {
        if(status == RemoteRepository.NetworkStatus.Progress) {
            networkDialog.show()
        } else {
            networkDialog.dismiss()
        }
    }

    private fun createDeletePlanDialog(): AlertDialog {
        return AlertDialog.Builder(context!!).apply {
            setMessage("Вы действительно хотите удалить задачу? Внимание: это действие нельзя отменить!")
            setPositiveButton("Удалить") { dialogInterface: DialogInterface, i: Int ->
                plansViewModel.deletePlan(planId)
                dialogInterface.dismiss()
            }
            setNegativeButton("Отмена", null)
        }.create()
    }

    private fun createNetworkDialog(): AlertDialog {
        return AlertDialog.Builder(context!!).apply {
            setCancelable(false)
            setView(R.layout.layout_network_dialog)
        }.create()
    }

    private fun createRefusePlanDialog(): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_comment_dialog, null, false)
        view.message.text = "Вы уверены, что хотите отказаться от выполнения задачи? Объяснитесь."
        val commentInput = view.input_comment

        val dialog = AlertDialog.Builder(context!!).apply {
            setView(view)
            setPositiveButton("Отказаться", null)
            setNegativeButton("Отмена", null)
        }.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val comment = commentInput.text?.toString()
                if(comment.isNullOrEmpty()) {
                    commentInput.setError("Комментарий обязателен")
                } else {
                    commentInput.setError(null)
                    plansViewModel.changePlanStatus(planId, comment, PlanStatus.Canceled)
                    dialog.dismiss()
                }
            }
        }

        return dialog
    }

    private fun createCompleteDialog(completed: Boolean): AlertDialog {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_comment_dialog, null, false)
        view.message.text = if(completed) "Вы уверены, что хотите пометить задачу как выполненную?" else
            "Вы уверены, что хотите пометить задачу как не выполненную?"

        val commentInput = view.input_comment

        return AlertDialog.Builder(context!!).apply {
            setView(view)
            setPositiveButton("Подтвердить") { dialogInterface: DialogInterface, i: Int ->
                val comment = commentInput.text?.toString()
                plansViewModel.changePlanStatus(
                    planId,
                    comment,
                    if(completed) PlanStatus.Completed else PlanStatus.NotCompleted)
                dialogInterface.dismiss()
            }
            setNegativeButton("Отмена", null)
        }.create()
    }

    companion object {

        @JvmStatic
        fun newInstance(planId: String) =
            PlanInfoFragment().apply {
                arguments = Bundle().apply {
                    putString(PLAN_ID, planId)
                }
            }
    }
}

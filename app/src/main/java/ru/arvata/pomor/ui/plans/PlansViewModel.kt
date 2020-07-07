package ru.arvata.pomor.ui.plans

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import ru.arvata.pomor.data.*
import ru.arvata.pomor.ui.getSiteDisplay
import ru.arvata.pomor.ui.getTankNumberDisplay
import ru.arvata.pomor.ui.getUserNameDisplay
import ru.arvata.pomor.ui.toDisplayString
import ru.arvata.pomor.util.*

class PlansViewModel : ViewModel() {
    val deletePlanStatus: LiveData<RemoteRepository.NetworkStatus> = RemoteRepository.deletePlanStatus
    val changePlanStatus: LiveData<RemoteRepository.NetworkStatus> = RemoteRepository.changePlanStatus

    private val _filtersLiveData: MutableLiveData<PlanFilters> = MutableLiveData()
    private var filter = PlanFilters(null, null, null, null)

    val filterTextLiveData: LiveData<String?> = _filtersLiveData.map {
        if(it == null) {
            null
        } else {
            val executor = it.type?.toDisplayString()
            val completion = it.status?.toDisplayString()
            val date = when (it.dateType) {
                PlanFilterDateType.Date -> it.date
                PlanFilterDateType.Today -> "Сегодня"
                PlanFilterDateType.Tomorrow -> "Завтра"
                else -> null
            }
            listOfNotNull(executor, completion, date).joinToString(", ")
        }
    }

    val plansDisplayLiveData = combine2(LocalRepository.plansLiveData, _filtersLiveData, ::filterPlans)

    private val selectedPlanId = MutableLiveData<String>()

    val planInfoLiveData: LiveData<PlanDisplay?> = combine2(LocalRepository.plansLiveData, selectedPlanId) {
            plans: List<Plan>?, planId: String? ->
        if(plans == null || planId == null) {
            return@combine2 null
        }

        val plan =  plans.find {
            it.id == planId
        }

        if(plan != null) {
            return@combine2 mapPlanDisplay(plan)
        } else {
            return@combine2 null
        }
    }

    init {
        applyFilters(null, null, null, null)
    }

    fun selectPlan(planId: String) {
        selectedPlanId.value = planId
    }

    fun setFilterPlanType(position: Int) {
        val type = when (position) {
            1 -> PlanFilterType.Executor
            2 -> PlanFilterType.Creator
            else -> null
        }

        applyFilters(type, filter.status, filter.dateType, filter.date)
    }

    fun setFilterPlanStatus(position: Int) {
        val status = when (position) {
            1 -> PlanStatus.Completed
            2 -> PlanStatus.NotCompleted
            else -> null
        }

        applyFilters(filter.type, status, filter.dateType, filter.date)
    }

    fun setFilterPlanDateType(position: Int) {
        val type = when(position) {
            1 -> PlanFilterDateType.Date
            2 -> PlanFilterDateType.Today
            3 -> PlanFilterDateType.Tomorrow
            else -> null
        }

        applyFilters(filter.type, filter.status, type, filter.date)
    }

    fun setFilterPlanDate(date: ddMMyyyy?) {
        applyFilters(filter.type, filter.status, filter.dateType, date)
    }

    fun deletePlan(planId: String) {
        RemoteRepository.deletePlan(planId)
    }

    fun changePlanStatus(planId: String, comment: String?, status: PlanStatus) {
        val user = UserRepository.loggedUserLiveData.value

        RemoteRepository.changePlanStatus(planId, comment, user, status)
    }

    private fun applyFilters(type: PlanFilterType?, status: PlanStatus?, dateType: PlanFilterDateType?, date: ddMMyyyy?) {
        filter = PlanFilters(type, status, dateType, date)
        _filtersLiveData.value = filter
    }

    private fun filterPlans(plans: List<Plan>?, filter: PlanFilters?): List<PlanDisplay>? {
        if(plans == null || filter == null) {
            return null
        }

        // ВСЕГДА вначале показывать просроченные задания

        val user = UserRepository.loggedUserLiveData.value

        val overdue = plans.filter { plan ->
            plan.isOverdue(user)
        }.sortedBy {
            it.dueTo
        }

        val others = plans.subtract(overdue).toList().sortedBy {
            it.dueTo
        }

        val filtered = (overdue + others).filter { plan ->
            val type = when {
                filter.type == PlanFilterType.Executor -> plan.executors.find { executor ->
                    user?.id == executor.id
                } != null
                filter.type == PlanFilterType.Creator -> plan.createdBy?.id == user?.id
                else -> true
            }

            val status = when {
                filter.status == PlanStatus.Completed -> plan.status == PlanStatus.Completed
                filter.status == PlanStatus.NotCompleted -> plan.status == PlanStatus.NotCompleted || plan.status == PlanStatus.Canceled
                else -> true
            }

            val date = when(filter.dateType) {
                PlanFilterDateType.Date -> if(filter.date != null) isDateInPeriod(filter.date, plan.dueFrom, plan.dueTo) else true
                PlanFilterDateType.Today -> isDateInPeriod(getToday(), plan.dueFrom, plan.dueTo)
                PlanFilterDateType.Tomorrow -> isDateInPeriod(getTomorrow(), plan.dueFrom, plan.dueTo)
                else -> true
            }

            type && status && date
        }

        return filtered.map(::mapPlanDisplay)
    }

    private fun mapPlanDisplay(plan: Plan): PlanDisplay {
        val user = UserRepository.loggedUserLiveData.value

        val tanks = plan.tanks.joinToString(", ") { pair ->
            val site = pair.second
            val tank = pair.first
            getSiteDisplay(site) + " • " + getTankNumberDisplay(tank)
        }

        val completeTime = if (plan.status == PlanStatus.Completed && plan.completedAt != null) {
            getHhmm_ddMMyyyyString(plan.completedAt)
        } else {
            "до ${getHhmm_ddMMyyyyString(plan.dueTo)}"
        }

        val executors = plan.executors.joinToString(", ") {
            getUserNameDisplay(it)
        }

        val createdBy = getUserNameDisplay(plan.createdBy)

        val isOverdue = plan.isOverdue(user)

        val canEdit = plan.canEdit(user)
        val canComplete = plan.canComplete(user)

        return PlanDisplay(plan.id, plan.title, tanks, plan.status, completeTime, plan.comment,
            executors, createdBy, plan.description, isOverdue, canEdit, canComplete)
    }
}

data class PlanFilters(val type: PlanFilterType?, val status: PlanStatus?,
                       val dateType: PlanFilterDateType?, val date: ddMMyyyy?)

data class PlanDisplay(val id: String, val title: String, val tanks: String, val status: PlanStatus,
                       val completeTime: String, val comment: String?, val executors: String, val createdBy: String,
                       val description: String?, val overdue: Boolean,
                       val canEdit: Boolean, val canComplete: Boolean)

enum class PlanFilterDateType {
    Date, Today, Tomorrow
}

enum class PlanFilterType {
    Executor, Creator;

    fun toDisplayString(): String {
        return when(this) {
            Executor -> "Я исполнитель"
            Creator -> "Я создатель"
        }
    }
}
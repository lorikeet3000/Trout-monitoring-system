package ru.arvata.pomor.ui.plans

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import ru.arvata.pomor.data.*
import ru.arvata.pomor.ui.getTankWithSiteDisplay
import ru.arvata.pomor.ui.getUserNameDisplay
import ru.arvata.pomor.util.*

class CreatePlanViewModel : ViewModel() {
    val createPlanStatus = RemoteRepository.sendPlanStatus

    val usersDisplay = LocalRepository.usersLiveData.map {
        it?.map {user ->
            getUserNameDisplay(user)
        }?.toTypedArray()
    }

    val allTanksDisplay = LocalRepository.allTanksWithSites.map {
        it?.map {pair ->
            getTankWithSiteDisplay(pair.first, pair.second)
        }?.toTypedArray()
    }

    private val executors = MutableLiveData<List<User>>()
    val executorsDisplay = executors.map {list ->
        list?.map {
            Pair(getUserNameDisplay(it), it.id)
        }
    }

    private val tanks = MutableLiveData<List<Pair<Tank, Site?>>>()
    val tanksDisplay = tanks.map {list ->
        list?.map {
            Pair(getTankWithSiteDisplay(it.first, it.second), it.first.id)
            // Pair<Tank with site name, Tank Id>
        }
    }

    private val dueFrom = MutableLiveData<isoDateTimeString>()
    val dueFromDisplay = dueFrom.map {
        getHhmm_ddMMyyyyString(it)
    }

    private val dueTo = MutableLiveData<isoDateTimeString>()
    val dueToDisplay = dueTo.map {
        getHhmm_ddMMyyyyString(it)
    }

    val titleLiveData = MutableLiveData<String?>()
    val descriptionLiveData = MutableLiveData<String?>()

    fun loadPlan(planId: String) {
        val plan = LocalRepository.plansLiveData.value?.find {
            it.id == planId
        }

        loge("load plan $plan")

        plan?.let {
            titleLiveData.value = it.title
            dueFrom.value = it.dueFrom
            dueTo.value = it.dueTo
            executors.value = it.executors
            tanks.value = it.tanks
            descriptionLiveData.value = it.description
        }
    }

    fun addExecutor(index: Int) {
        val list = executors.value ?: emptyList()
        // ищем среди всех юзеров одного с нужным index
        // если его еще нет в списке исполнителей,
        // добавляем его в список
        LocalRepository.usersLiveData.value?.let {users ->
            if(index < users.size) {
                val executor = users[index]
                if(!list.contains(executor)) {
                    val newExecutors = mutableListOf<User>()
                    newExecutors.addAll(list)
                    newExecutors.add(executor)
                    executors.value = newExecutors
                }
            }
        }
    }

    fun removeExecutor(id: String) {
        val list = executors.value?.filter {
            it.id != id
        }
        executors.value = list
    }

    fun addTank(index: Int) {
        val list = tanks.value ?: emptyList()
        LocalRepository.allTanksWithSites.value?.let {pairs ->
            if(index < pairs.size) {
                val tank = pairs[index]
                if(!list.contains(tank)) {
                    val newTanks = mutableListOf<Pair<Tank, Site?>>()
                    newTanks.addAll(list)
                    newTanks.add(tank)
                    tanks.value = newTanks
                }
            }
        }
    }

    fun removeTank(id: String) {
        val list = tanks.value?.filter {
            it.first.id != id
        }
        tanks.value = list
    }

    fun setDueFromTime(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val iso = getIsoDateTimeString(year, month, day, hour, minute)
        dueFrom.value = iso
    }

    fun setDueToTime(year: Int, month: Int, day: Int, hour: Int, minute: Int) {
        val iso = getIsoDateTimeString(year, month, day, hour, minute)
        dueTo.value = iso
    }

    fun confirmPlan(planId: String?, title: String?, description: String?): ValidationError? {
        if(title == null || title.isEmpty() || title.isBlank()) {
            return ValidationError.NoTitle
        }
        val createdBy = UserRepository.loggedUserLiveData.value!!.id
        val dueFrom = dueFrom.value ?: return ValidationError.NoDueFrom
        val dueTo = dueTo.value ?: return ValidationError.NoDueTo
        val executorIds = executors.value?.map {
            it.id
        } ?: return ValidationError.NoExecutors
        val tankIds = tanks.value?.map {
            it.first.id
        } ?: return ValidationError.NoTanks

        if(planId != null) {
            RemoteRepository.editPlan(planId, dueFrom, dueTo, executorIds, tankIds, title, description)
        } else {
            RemoteRepository.sendNewPlan(createdBy, dueFrom, dueTo, executorIds, tankIds, title, description)
        }
        return null
    }
}

enum class ValidationError {
    NoTitle, NoDueFrom, NoDueTo, NoExecutors, NoTanks
}
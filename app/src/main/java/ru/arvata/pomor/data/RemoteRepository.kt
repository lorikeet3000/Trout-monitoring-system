package ru.arvata.pomor.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import ru.arvata.pomor.data.database.*
import ru.arvata.pomor.data.database.TroutTypeConverters.PLAN_STATUS_CANCELED
import ru.arvata.pomor.data.database.TroutTypeConverters.PLAN_STATUS_COMPLETED
import ru.arvata.pomor.data.database.TroutTypeConverters.PLAN_STATUS_NOT_COMPLETED
import ru.arvata.pomor.data.database.TroutTypeConverters.planStatusToString
import ru.arvata.pomor.data.network.*
import ru.arvata.pomor.util.SingleLiveEvent
import ru.arvata.pomor.util.getIsoDateTimeString
import ru.arvata.pomor.util.loge
import java.lang.Exception
import java.util.*

object RemoteRepository {
    enum class NetworkEvent {
        IndicatorsSuccess, IndicatorsError, SummarySuccess, SummaryError, PlanSuccess, PlanError
    }

    enum class NetworkStatus {
        Progress, Success, Error
    }

    private val DATA_REFRESH_PERIOD_DAYS = 30

    val networkEvents = SingleLiveEvent<NetworkEvent>()
    val sendPlanStatus = SingleLiveEvent<NetworkStatus>()
    val deletePlanStatus = SingleLiveEvent<NetworkStatus>()
    val changePlanStatus = SingleLiveEvent<NetworkStatus>()

    private val _sendIndicatorsIds = MutableLiveData<List<IndicatorEntity>>()
    val sendIndicatorsIds: LiveData<List<IndicatorEntity>> = _sendIndicatorsIds

    // todo add coroutines scope

    fun syncWithServer() {
        // 1. if there are not uploaded indicators - upload them
        // 2. after that - get summary

        GlobalScope.launch(Dispatchers.IO) {
            val notUploadedIndicators = LocalRepository.getNotUploadedIndicatorsSync()
            val indicatorsResult = sendIndicators(notUploadedIndicators)
            if(indicatorsResult != null) {
                if(indicatorsResult.isNotEmpty()) {
                    _sendIndicatorsIds.postValue(indicatorsResult)
                    networkEvents.postValue(RemoteRepository.NetworkEvent.IndicatorsSuccess)
                }
            } else {
                networkEvents.postValue(RemoteRepository.NetworkEvent.IndicatorsError)
            }

            updateSummary()
        }
    }

    fun sendNewPlan(createdBy: String,
                    dueFrom: String,
                    dueTo: String,
                    executorIds: List<String>,
                    tankIds: List<String>,
                    title: String,
                    description: String?) {
        sendPlanStatus.postValue(RemoteRepository.NetworkStatus.Progress)

        val plan = CreatePlanNetwork(createdBy, dueFrom, dueTo, executorIds, tankIds, title, description)

        GlobalScope.launch {
            try {
                val response = Network.api.sendPlan(plan)
                if (response.isSuccessful) {
                    networkEvents.postValue(RemoteRepository.NetworkEvent.PlanSuccess)
                    sendPlanStatus.postValue(RemoteRepository.NetworkStatus.Success)
                } else {
                    networkEvents.postValue(RemoteRepository.NetworkEvent.PlanError)
                    sendPlanStatus.postValue(RemoteRepository.NetworkStatus.Error)
                }
            } catch (e: Exception) {
                networkEvents.postValue(RemoteRepository.NetworkEvent.PlanError)
                sendPlanStatus.postValue(RemoteRepository.NetworkStatus.Error)
            }
        }
        Handler().postDelayed({
            syncWithServer()
        }, 500)
    }

    fun editPlan(planId: String,
                 dueFrom: String,
                 dueTo: String,
                 executorIds: List<String>,
                 tankIds: List<String>,
                 title: String,
                 description: String?) {
        sendPlanStatus.postValue(RemoteRepository.NetworkStatus.Progress)

        val plan = CreatePlanNetwork("", dueFrom, dueTo, executorIds, tankIds, title, description)

        GlobalScope.launch {
            try {
                val response = Network.api.editPlan(planId, plan)
                if (response.isSuccessful) {
                    networkEvents.postValue(RemoteRepository.NetworkEvent.PlanSuccess)
                    sendPlanStatus.postValue(RemoteRepository.NetworkStatus.Success)
                } else {
                    networkEvents.postValue(RemoteRepository.NetworkEvent.PlanError)
                    sendPlanStatus.postValue(RemoteRepository.NetworkStatus.Error)
                }
            } catch (e: Exception) {
                networkEvents.postValue(RemoteRepository.NetworkEvent.PlanError)
                sendPlanStatus.postValue(RemoteRepository.NetworkStatus.Error)
            }
        }
        Handler().postDelayed({
            syncWithServer()
        }, 500)
    }

    fun login(login: String, password: String, onSuccess: (userId: String) -> Unit, onError: () -> Unit) {
        GlobalScope.launch {
            try {
                val response = Network.api.login(CredentialsNetwork(login, password))
                val user = response.body()
                if (response.code() == 200 && user != null) {
                    GlobalScope.launch(Dispatchers.Main) {
                        onSuccess(user.id)
                    }
                } else {
                    GlobalScope.launch(Dispatchers.Main) {
                        onError()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                GlobalScope.launch(Dispatchers.Main) {
                    onError()
                }
            }
        }
    }

    fun deletePlan(planId: String) {
        deletePlanStatus.postValue(RemoteRepository.NetworkStatus.Progress)
        GlobalScope.launch {
            try {
                val response = Network.api.deletePlan(planId)
                if(response.isSuccessful) {
                    updateSummary()
                    deletePlanStatus.postValue(RemoteRepository.NetworkStatus.Success)
                } else {
                    deletePlanStatus.postValue(RemoteRepository.NetworkStatus.Error)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                deletePlanStatus.postValue(RemoteRepository.NetworkStatus.Error)
            }
        }
    }

    fun changePlanStatus(planId: String, comment: String?, completedBy: User?, status: PlanStatus) {
        changePlanStatus.value = RemoteRepository.NetworkStatus.Progress
        GlobalScope.launch {
            try {
                val planNetwork = PlanStatusNetwork(comment, completedBy?.id, planStatusToString(status))
                val response = Network.api.changePlanStatus(planId, planNetwork)
                if(response.isSuccessful) {
                    updateSummary()
                    changePlanStatus.postValue(RemoteRepository.NetworkStatus.Success)
                } else {
                    changePlanStatus.postValue(RemoteRepository.NetworkStatus.Error)
                }
            } catch (e: Exception) {
                changePlanStatus.postValue(RemoteRepository.NetworkStatus.Error)
            }
        }
    }

    private suspend fun updateSummary() {
        val summary = getRemoteSummary()
        if(summary != null) {
            saveSummary(summary)
            networkEvents.postValue(RemoteRepository.NetworkEvent.SummarySuccess)
        } else {
            networkEvents.postValue(RemoteRepository.NetworkEvent.SummaryError)
        }
    }

    private suspend fun sendIndicators(indicators: List<IndicatorEntity>): List<IndicatorEntity>? {
        if(indicators.isEmpty()) {
            return emptyList()
        }

        val list = indicators.map {
            IndicatorNetwork("", it.indicator, it.timestamp, it.tankId, it.type,
                it.feedProducerId, it.feedCoef, it.feedPeriodFrom, it.feedPeriodTo,
                it.movingTankId, it.movingReason, it.catchReason)
        }

        try {
            val response = Network.api.sendIndicators(list)
            loge("send notUploadedIndicators: ${response.isSuccessful}")

            if(response.isSuccessful) {
                return indicators
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private suspend fun getRemoteSummary(): SummaryNetwork? {
        val calEnd = Calendar.getInstance()
        calEnd.add(Calendar.DAY_OF_MONTH, 1) // смотрим в будущее (вдруг время на сервере кривое)
        val calStart = Calendar.getInstance()
        calStart.add(Calendar.DAY_OF_MONTH, -DATA_REFRESH_PERIOD_DAYS)
        val start = getIsoDateTimeString(calStart.time)
        val end = getIsoDateTimeString(calEnd.time)

        try {
            val users = Network.api.getUsers()
            loge("users: $users")

            val response = Network.api.getData(start, end)
            loge("get remote data: ${response.isSuccessful}")
            if(response.isSuccessful) {
                val summary = response.body()
                return summary
            } //todo check error code

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun saveSummary(summaryNetwork: SummaryNetwork) {
        val users = summaryNetwork.users.map {
            UserEntity(it.id, it.name, it.login, it.role)
        }

        val feedProducers = summaryNetwork.feedProducers.map {
            FeedProducerEntity(it.id, it.name)
        }

        val sites = summaryNetwork.sites.map {
            SiteEntity(it.id, it.name)
        }

        val events = summaryNetwork.events.map {
            EventEntity(it.id, it.userId, it.tankId, it.timestamp, it.description, it.siteId)
        }

        val tanks = summaryNetwork.tanks.map {
            TankEntity(it.id, it.siteId, it.number, it.fishAmount)
        }

        val tankIndicators = mutableListOf<IndicatorEntity?>()
        for (tank in summaryNetwork.tanks) {
            tankIndicators.add(tank.indicators.fishWeight?.toEntity(true, null, null))
            tankIndicators.add(tank.indicators.temperature?.toEntity(true, null, null))
            tankIndicators.add(tank.indicators.oxygen?.toEntity(true, null, null))
            tankIndicators.add(tank.indicators.feeding?.toEntity(true, null, null))
            tankIndicators.add(tank.indicators.seeding?.toEntity(true, null, null))
            tankIndicators.add(tank.indicators.mortality?.toEntity(true, null, null))
            tankIndicators.add(tank.indicators.moving?.toEntity(true, null, null))
            tankIndicators.add(tank.indicators.catch?.toEntity(true, null, null))
        }

        val indicators = (summaryNetwork.temperatureIndicators + summaryNetwork.oxygenIndicators).map {
            it.toEntity(true, null, null)
        } + tankIndicators.filterNotNull()

        for (plan in summaryNetwork.plans) {
//            loge("plan $plan")
        }

        val plans = summaryNetwork.plans.map {
            PlanEntity(it.id, it.title, it.description, it.createdAt, it.createdBy, it.dueFrom, it.dueTo,
                it.executorIds, it.tankIds, "", it.status, it.completedAt, it.completedBy, it.comment)
        }

        LocalDataSource.saveSummary(users, feedProducers, sites, tanks, events, indicators, plans)
    }

    fun cancelAllCRequests() {
    }
}
package ru.arvata.pomor.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import ru.arvata.pomor.data.database.IndicatorEntity
import ru.arvata.pomor.data.database.TroutTypeConverters
import ru.arvata.pomor.util.loge

object LocalRepository {
    private val _selectedSiteLiveData = MutableLiveData<Site>()
    val selectedSiteLiveData: LiveData<Site> = _selectedSiteLiveData

    val sitesLiveData: LiveData<List<Site>> = LocalDataSource.sites

    val eventsLiveData: LiveData<List<Event>?> = LocalDataSource.events

    val allTanksLiveData: LiveData<List<Tank>?> = LocalDataSource.allTanks

    val siteTanksLiveData: LiveData<List<Tank>?> = LocalDataSource.siteTanksLiveData

    val allTanksWithSites = LocalDataSource.allTanksWithSites

    val siteTanksWithSites = LocalDataSource.siteTanksWithSites

    val usersLiveData: LiveData<List<User>> = LocalDataSource.users

    val feedProducersLiveData: LiveData<List<FeedProducer>> = LocalDataSource.feedProducers

    val temperatureIndicatorsLiveData: LiveData<List<Indicator>?> = LocalDataSource.temperatureIndicators

    val oxygenIndicatorsLiveData: LiveData<List<Indicator>?> = LocalDataSource.oxygenIndicators

    val plansLiveData: LiveData<List<Plan>?> = LocalDataSource.plans

    fun getNotUploadedIndicatorsSync(): List<IndicatorEntity> {
        return LocalDataSource.getNotUploadedIndicatorsSync()
    }

    private val notUploadedIndicatorsObserver = Observer { list: List<IndicatorEntity>? ->
        /*
            check if there are any not uploaded indicators
            and upload them to server
         */

        if(list == null || list.isEmpty()) {
            return@Observer
        }
        loge("we have some not uploaded indicators:")
        list.forEach {
            loge("$it")
        }
        RemoteRepository.syncWithServer()
    }

    private val uploadedIndicatorsObserver = Observer {list: List<IndicatorEntity>? ->
        /*
            observe what indicators were sent by remote repository
            and delete them from DB
         */

        if(list != null) {
            LocalDataSource.deleteIndicators(list)
        }
    }

    init {
        LocalDataSource.notUploadedIndicators.observeForever(notUploadedIndicatorsObserver)
        RemoteRepository.sendIndicatorsIds.observeForever(uploadedIndicatorsObserver)
    }

    fun selectSite(site: Site?) {
        _selectedSiteLiveData.value = site
    }

    fun saveIndicatorsList(userId: String?, indicators: List<Indicator>) {
       val entities = indicators.map {
           val type = TroutTypeConverters.indicatorTypeToString(it.type)
            IndicatorEntity(
                it.id,
                it.indicator.toFloat(),
                it.time,
                it.tank.id,
                type,
                false,
                userId,
                it.tank.siteId,
                it.feedProducer?.id,
                it.feedCoef,
                it.feedPeriodFrom,
                it.feedPeriodTo,
                it.movingTank?.id,
                it.movingReason,
                it.catchReason)
        }
        LocalDataSource.saveIndicators(entities)
    }
}

/*
    Activity/Fragment
    EventDisplay()

    ViewModel, LocalRepository
    Event()

    LocalDataSource
    EventEntity()

 */
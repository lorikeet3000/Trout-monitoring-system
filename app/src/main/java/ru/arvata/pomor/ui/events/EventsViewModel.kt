package ru.arvata.pomor.ui.events

import android.arch.lifecycle.*
import ru.arvata.pomor.data.*
import ru.arvata.pomor.ui.*
import ru.arvata.pomor.util.*

class EventsViewModel : ViewModel() {
    private val eventsLiveData: LiveData<List<Event>?> = LocalRepository.eventsLiveData
    private val filtersLiveData: MutableLiveData<EventFilters> = MutableLiveData()

    val eventsDisplay = combine2(eventsLiveData, filtersLiveData, ::combineEventFilters)

    val filterTextDisplay = filtersLiveData.map {
        val userDisplay = if(it?.user != null) getUserNameDisplay(it.user) else null
        val site = if(it?.site != null) getSiteDisplay(it.site) else null
        val tank = if(it?.tank != null) {
            if(it.site == null) {
                getTankWithSiteDisplay(it.tank.first, it.tank.second)
            } else {
                getTankNumberDisplay(it.tank.first)
            }
        } else null

        val date = if(it.dateType != null) {
            when {
                it.dateType == EventFilterDateType.Date -> it.date
                it.dateType == EventFilterDateType.Today -> "Сегодня"
                it.dateType == EventFilterDateType.Yesterday -> "Вчера"
                else -> null
            }
        } else null

        listOfNotNull(userDisplay, site, tank, date).joinToString()
    }

    private var filter: EventFilters = EventFilters()

    init {
        applyFilters(null, null, null, null, null)
    }

    val users: LiveData<Array<String>> = LocalRepository.usersLiveData.map { list ->
        val result = mutableListOf("Все")
        result += list.map { user ->
            getUserNameDisplay(user)
        }
        result.toTypedArray()
    }

    val sites: LiveData<Array<String>> = LocalRepository.sitesLiveData.map { list ->
        val result = mutableListOf("Все")
        result += list.map { site ->
            getSiteDisplay(site)
        }
        result.toTypedArray()
    }

    private val tanksChanged = SingleLiveEvent<Void>()
    // если поменялся фильтр участка, то нужно получить новый список садков, принадлежащих этому участку
    // и сбросить выбранный садок (произойдет автоматически при selector_tank.init() )
    private val tanks = combine2(LocalRepository.allTanksWithSites, tanksChanged) {
            pairs: List<Pair<Tank, Site?>>?, _: Void? ->
        val id = filter.site?.id
        if(id == null) {
            pairs
        } else {
            pairs?.filter {
                it.first.siteId == id
            }
        }
    }

    val tanksDisplay = tanks.map { pairs ->
        val result = mutableListOf("Все")

        val list = if(filter.site == null) {
            pairs?.map {
                getTankWithSiteDisplay(it.first, it.second)
            }
        } else {
            pairs?.map {
                getTankNumberDisplay(it.first)
            }
        }
        if(list != null) {
            result += list
        }

        result.toTypedArray()
    }

    private fun combineEventFilters(events: List<Event>?,
                                    filter: EventFilters?): List<EventDisplay>? {
        if(events == null || filter == null) {
            return null
        }

        return events.filter { event ->

            if(filter.user != null && filter.user.id != event.user?.id) {
                return@filter false
            }

            if(filter.site != null && filter.site.id != event.site?.id) {
                return@filter false
            }

            if(filter.tank != null && filter.tank.first.id != event.tank?.id) {
                return@filter false
            }

            if(filter.dateType != null ) {
                return@filter when(filter.dateType) {
                    EventFilterDateType.Date -> if(filter.date != null) isDatesEqual(filter.date, event.time) else true
                    EventFilterDateType.Today -> isToday(event.time)
                    EventFilterDateType.Yesterday -> isYesterday(event.time)
                }
            }

            true
        }.map {
            EventDisplay(
                getRelativeDateTimeString(it.time),
                getUserNameDisplay(it.user),
                getTankWithSiteDisplay(it.tank, it.site),
                getEventDescriptionDisplay(it.description)
            )
        }
    }

    fun setUserFilter(position: Int, text: String?) {
        val user = if(position == 0) { null } else {
            LocalRepository.usersLiveData.value?.get(position - 1)
        }

        applyFilters(user, filter.site, filter.tank, filter.dateType, filter.date)
    }

    fun setSiteFilter(position: Int, text: String?) {
        val site = if(position == 0) { null } else {
            LocalRepository.sitesLiveData.value?.get(position - 1)
        }

        applyFilters(filter.user, site, filter.tank, filter.dateType, filter.date)
        tanksChanged.call()
    }

    fun setTankFilter(position: Int, text: String?) {
        val tank = if(position == 0) { null } else {
            LocalRepository.allTanksWithSites.value?.get(position - 1)
        }

        if(tank == filter.tank) {
            return
        }

        applyFilters(filter.user, filter.site, tank, filter.dateType, filter.date)
    }

    fun setDateTypeFilter(position: Int) {
        val type = when(position) {
            1 -> EventFilterDateType.Date
            2 -> EventFilterDateType.Today
            3 -> EventFilterDateType.Yesterday
            else -> null
        }

        applyFilters(filter.user, filter.site, filter.tank, type, filter.date)
    }

    fun setDateFilter(date: ddMMyyyy?) {
        applyFilters(filter.user, filter.site, filter.tank, filter.dateType, date)
    }

    private fun applyFilters(user: User?, site: Site?, tank: Pair<Tank, Site?>?, dateType: EventFilterDateType?, date: ddMMyyyy?) {
        filter = EventFilters(user, site, tank, dateType, date)
        filtersLiveData.value = filter
    }
}

enum class EventFilterDateType {
    Date, Today, Yesterday
}

data class EventDisplay(val time: relativeDateTimeString, val user: String, val tank: String, val description: String)

data class EventFilters(val user: User? = null,
                        val site: Site? = null,
                        val tank: Pair<Tank, Site?>? = null,
                        val dateType: EventFilterDateType? = null,
                        val date: ddMMyyyy? = null)
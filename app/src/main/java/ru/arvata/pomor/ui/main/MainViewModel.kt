package ru.arvata.pomor.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import ru.arvata.pomor.data.LocalRepository
import ru.arvata.pomor.data.RemoteRepository
import ru.arvata.pomor.data.UserRepository
import ru.arvata.pomor.util.map

class MainViewModel : ViewModel() {
    val networkEvents: LiveData<RemoteRepository.NetworkEvent> = RemoteRepository.networkEvents

    val sitesLiveData: LiveData<Array<String>> = LocalRepository.sitesLiveData.map {list ->
        val result = mutableListOf("Все участки")
        result += list.map {
            it.name
        }
        result.toTypedArray()
    }

    private val _selectedSiteLiveData = MutableLiveData<Int>()
    val selectedSiteLiveData = _selectedSiteLiveData

    val loginStateLiveData = UserRepository.loginStateLiveData
    val loggedUserLiveData: LiveData<UserDisplay?> = Transformations.map(UserRepository.loggedUserLiveData) {
        if(it != null) {
            UserDisplay(it.name, it.role)
        } else null
    }

    /*
        position = 0 => all sites
     */
    fun setSite(position: Int) {
        val site = if(position == 0) {
            null
        } else {
            LocalRepository.sitesLiveData.value?.get(position - 1)
        }
        LocalRepository.selectSite(site)
        _selectedSiteLiveData.value = position
    }

    fun logout() {
        UserRepository.logout()
    }

    fun refreshData() {
        RemoteRepository.syncWithServer()
    }
}

data class UserDisplay(val name: String, val role: String)
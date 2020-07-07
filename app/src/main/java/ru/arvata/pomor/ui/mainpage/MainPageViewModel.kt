package ru.arvata.pomor.ui.mainpage

import android.arch.lifecycle.ViewModel
import ru.arvata.pomor.data.LocalRepository

class MainPageViewModel : ViewModel() {
    val sitesLiveData = LocalRepository.selectedSiteLiveData
}
package ru.arvata.pomor.ui.login

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import ru.arvata.pomor.data.LoginError
import ru.arvata.pomor.data.LoginState
import ru.arvata.pomor.data.UserRepository

class LoginViewModel : ViewModel() {
    val loginStateLiveData: LiveData<LoginState> = UserRepository.loginStateLiveData
    val loginErrorLiveData: LiveData<LoginError> = UserRepository.loginErrorLiveData

    fun login(login: String, password: String) {
        UserRepository.login(login, password)
    }
}

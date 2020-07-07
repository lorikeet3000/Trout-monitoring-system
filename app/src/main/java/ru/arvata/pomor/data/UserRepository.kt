package ru.arvata.pomor.data

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.arvata.pomor.data.network.CredentialsNetwork
import ru.arvata.pomor.data.network.Network
import ru.arvata.pomor.data.network.UserNetwork
import ru.arvata.pomor.util.*

object UserRepository {
    private val _loginStateLiveData = MutableLiveData<LoginState>()
    val loginStateLiveData: LiveData<LoginState> = _loginStateLiveData

    val loginErrorLiveData = SingleLiveEvent<LoginError>()

    val loggedUserLiveData: LiveData<User?> = UserLocalDataSource.loggedUserLiveData

    init {
        _loginStateLiveData.value = if (UserLocalDataSource.isLoggedUser()) {
            //todo check cookie expiration date
            LoginState.Logged
        } else {
            LoginState.NotLogged
        }
    }

    private fun onLoginSuccess(userId: String) {
        UserLocalDataSource.saveLoggedUser(userId)
        RemoteRepository.syncWithServer()
        _loginStateLiveData.value = LoginState.Logged
    }

    private fun onLoginError() {
        loginErrorLiveData.value = LoginError.ServerError
        _loginStateLiveData.value = LoginState.NotLogged
    }

    fun login(login: String, password: String) {
        _loginStateLiveData.value = LoginState.InProgress
        RemoteRepository.login(login, password, ::onLoginSuccess, ::onLoginError)
    }

    fun isLoggedIn(): Boolean = UserLocalDataSource.isLoggedUser()

    fun logout() {
        _loginStateLiveData.value = LoginState.NotLogged
        UserLocalDataSource.deleteLoggedUser()
    }
}
enum class LoginError {
    ServerError
}

enum class LoginState {
    NotLogged, InProgress, Logged
}
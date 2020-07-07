package ru.arvata.pomor.data

import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import okhttp3.Cookie
import okhttp3.HttpUrl
import ru.arvata.pomor.util.App
import ru.arvata.pomor.util.editPreferences
import ru.arvata.pomor.util.getPreferences

private const val PREF_SESSION_ID = "sessionId"

object UserLocalDataSource {
    private const val PREFS_LOGGED_USER_ID = "logged_user_id"

    private val _loggedUserLiveData = MutableLiveData<String?>()

    val loggedUserLiveData = MediatorLiveData<User?>()

    init {
        loggedUserLiveData.addSource(_loggedUserLiveData) {
            loggedUserLiveData.value = combineLoggedUser(it, LocalRepository.usersLiveData.value)
        }

        loggedUserLiveData.addSource(LocalRepository.usersLiveData) {
            loggedUserLiveData.value = combineLoggedUser(_loggedUserLiveData.value, it)
        }

        _loggedUserLiveData.value = getPreferences(App.appContext).getString(PREFS_LOGGED_USER_ID, null)
    }

    private fun combineLoggedUser(loggedUserId : String?, usersList: List<User>?): User? {
        return usersList?.find {
            it.id == loggedUserId
        }
    }

    fun saveLoggedUser(userId: String) {
        editPreferences(App.appContext) {
            it.putString(PREFS_LOGGED_USER_ID, userId)
        }
        _loggedUserLiveData.value = userId
    }

    fun isLoggedUser(): Boolean {
        return _loggedUserLiveData.value != null
    }

    fun deleteLoggedUser() {
        editPreferences(App.appContext) {
            it.remove(PREFS_LOGGED_USER_ID)
            it.remove(PREF_SESSION_ID)
        }
        _loggedUserLiveData.value = null
    }
}

fun saveSessionCookie(cookie: Cookie) {
    editPreferences(App.appContext) {
        it.putString(PREF_SESSION_ID, cookie.value())
    }
}

fun getSessionCookie(url: HttpUrl): Cookie? {
    val sessionId = getPreferences(App.appContext).getString(PREF_SESSION_ID, null)

    if(sessionId == null) {
        return null
    }

    return Cookie.Builder().apply {
        name("session")
        value(sessionId)
        domain(url.host())
    }.build()
}

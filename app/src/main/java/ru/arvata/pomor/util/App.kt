package ru.arvata.pomor.util

import android.app.Application
import android.content.Context
import ru.arvata.pomor.data.RemoteRepository


class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        SyncWorker.initSyncWork()

        registerNetworkReceiver(this) {
            loge("network available")
            RemoteRepository.syncWithServer()
        }
    }

    companion object {
        private lateinit var instance: App

        val appContext: Context
            get() = instance.applicationContext
    }
}